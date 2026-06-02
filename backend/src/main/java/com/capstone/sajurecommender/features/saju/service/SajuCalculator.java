package com.capstone.sajurecommender.features.saju.service;

import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.Pillar;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Core engine for calculating the Four Pillars of Destiny (사주팔자) 
 * from a given birth date and time.
 *
 * Calculation rules:
 * - Year pillar: Based on 60 Sexagenary cycle from (year - 4) % 60
 * - Month pillar: Based on Solar Terms (절기), not calendar months
 * - Day pillar: Based on days elapsed from a reference date (Jan 1, 1900 = 甲子)
 * - Hour pillar: Based on 둔간법 (Stem Hiding Method) using the Day Stem
 */
@Slf4j
@Service
public class SajuCalculator {

    // Reference date: January 1, 1900 is 갑자(甲子) day
    private static final LocalDate REFERENCE_DATE = LocalDate.of(1900, 1, 1);
    // Reference day's stem index = 0 (甲), branch index = 0 (子)
    private static final int REFERENCE_STEM_INDEX = 0;
    private static final int REFERENCE_BRANCH_INDEX = 0;

    /**
     * Calculate the complete Four Pillars for a given birth datetime.
     */
    public FourPillars calculate(LocalDateTime birthDateTime) {
        int year = birthDateTime.getYear();
        int month = birthDateTime.getMonthValue();
        int day = birthDateTime.getDayOfMonth();
        int hour = birthDateTime.getHour();
        LocalDate date = birthDateTime.toLocalDate();

        // Determine the Saju year (절기 기준: 입춘 이전이면 전년도)
        int sajuYear = determineSajuYear(year, month, day);
        int sajuMonth = determineSajuMonth(month, day);

        Pillar yearPillar = calculateYearPillar(sajuYear);
        Pillar monthPillar = calculateMonthPillar(sajuYear, sajuMonth);
        Pillar dayPillar = calculateDayPillar(date);
        Pillar hourPillar = calculateHourPillar(dayPillar.stem(), hour);

        log.info("Calculated Four Pillars for {}: 년{} 월{} 일{} 시{}",
                birthDateTime, yearPillar.toKoreanString(), monthPillar.toKoreanString(),
                dayPillar.toKoreanString(), hourPillar.toKoreanString());

        return new FourPillars(yearPillar, monthPillar, dayPillar, hourPillar);
    }

    /**
     * Determine the Saju year based on solar terms.
     * Before 입춘 (Ipchun, ~Feb 4), the previous year is used.
     */
    private int determineSajuYear(int year, int month, int day) {
        int dayOfYear = LocalDate.of(year, month, day).getDayOfYear();
        // 입춘 is approximately day 35 (Feb 4)
        if (dayOfYear < SajuConstants.SOLAR_TERM_MONTH_START_DAYS[0]) {
            return year - 1;
        }
        return year;
    }

    /**
     * Determine the Saju month (1-12) based on solar terms.
     * Month 1 starts at 입춘 (~Feb 4), Month 2 at 경칩 (~Mar 6), etc.
     */
    private int determineSajuMonth(int month, int day) {
        int dayOfYear = LocalDate.of(2000, month, day).getDayOfYear(); // use a non-leap year for consistency
        int[] termDays = SajuConstants.SOLAR_TERM_MONTH_START_DAYS;

        // Check from month 12 backward to month 1
        // Month 12 (丑月) starts at 소한 (~Jan 6) — wraps around
        if (dayOfYear < termDays[0]) {
            return 12; // Before 입춘 = still month 12 (丑月)
        }

        for (int i = 10; i >= 0; i--) {
            if (dayOfYear >= termDays[i]) {
                return i + 1;
            }
        }

        return 12;
    }

    /**
     * Calculate the Year Pillar (년주).
     * The Sexagenary cycle started from year 4 CE (甲子年).
     */
    Pillar calculateYearPillar(int year) {
        int offset = year - 4;
        int stemIndex = Math.floorMod(offset, 10);
        int branchIndex = Math.floorMod(offset, 12);

        return new Pillar(
                HeavenlyStem.fromIndex(stemIndex),
                EarthlyBranch.fromIndex(branchIndex)
        );
    }

    /**
     * Calculate the Month Pillar (월주).
     * The month stem is determined by the year stem using the 오호둔법 (Five Tigers Rule).
     * The month branch always starts from 寅 (IN) for month 1.
     */
    Pillar calculateMonthPillar(int sajuYear, int sajuMonth) {
        // Month branch: month 1 = 寅(IN, index 2), month 2 = 卯(MYO, index 3), ...
        int branchIndex = (sajuMonth + 1) % 12; // month 1 → index 2 (寅)

        // Month stem: determined by year stem using 월간 결정표
        int yearStemIndex = Math.floorMod(sajuYear - 4, 10);
        int monthStemOffset = SajuConstants.MONTH_STEM_OFFSET[yearStemIndex % 5];
        int stemIndex = (monthStemOffset + sajuMonth - 1) % 10;

        return new Pillar(
                HeavenlyStem.fromIndex(stemIndex),
                EarthlyBranch.fromIndex(branchIndex)
        );
    }

    /**
     * Calculate the Day Pillar (일주).
     * Based on the number of days elapsed from the reference date.
     */
    Pillar calculateDayPillar(LocalDate date) {
        long daysBetween = ChronoUnit.DAYS.between(REFERENCE_DATE, date);

        int stemIndex = Math.floorMod((int) (REFERENCE_STEM_INDEX + daysBetween), 10);
        int branchIndex = Math.floorMod((int) (REFERENCE_BRANCH_INDEX + daysBetween), 12);

        return new Pillar(
                HeavenlyStem.fromIndex(stemIndex),
                EarthlyBranch.fromIndex(branchIndex)
        );
    }

    /**
     * Calculate the Hour Pillar (시주).
     * Uses 둔간법 (Stem Hiding Method): the hour stem is determined by the day stem.
     * 
     * Rule: 
     * 갑/기일 → 갑자시 시작 (offset 0)
     * 을/경일 → 병자시 시작 (offset 2)
     * 병/신일 → 무자시 시작 (offset 4)
     * 정/임일 → 경자시 시작 (offset 6)
     * 무/계일 → 임자시 시작 (offset 8)
     */
    Pillar calculateHourPillar(HeavenlyStem dayStem, int hour) {
        EarthlyBranch hourBranch = EarthlyBranch.fromHour(hour);

        // 둔간법: day stem index % 5 determines the offset
        int dayOffset = dayStem.getIndex() % 5;
        int hourStemOffset = (dayOffset * 2) % 10;
        int stemIndex = (hourStemOffset + hourBranch.getIndex()) % 10;

        return new Pillar(
                HeavenlyStem.fromIndex(stemIndex),
                hourBranch
        );
    }
}
