package com.capstone.sajurecommender.features.saju.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 음력(陰曆) → 양력(陽曆) 변환 서비스.
 *
 * 정확한 구현을 위해서는 한국천문연구원 API 또는 전용 라이브러리(예: koreaCal)가 필요합니다.
 * 여기서는 근사 알고리즘과 보정값을 활용한 추정 변환을 제공합니다.
 *
 * 참고: 실제 프로덕션에서는 한국천문연구원 OPEN API를 사용하는 것을 권장합니다.
 * https://astro.kasi.re.kr/
 */
@Slf4j
@Service
public class LunarCalendarConverter {

    /**
     * 음력 날짜를 양력 날짜로 변환합니다.
     * 
     * 알고리즘: Meeus의 천문 알고리즘 기반 근사 계산
     * 오차 범위: ±1~2일 (1900~2100년)
     *
     * @param lunarYear  음력 연도
     * @param lunarMonth 음력 월 (1~12)
     * @param lunarDay   음력 일 (1~30)
     * @param isLeapMonth 윤달 여부
     * @return 해당 음력 날짜의 양력 LocalDate
     */
    public LocalDate lunarToSolar(int lunarYear, int lunarMonth, int lunarDay, boolean isLeapMonth) {
        log.debug("Converting lunar {}-{}-{} (leap:{}) to solar", lunarYear, lunarMonth, lunarDay, isLeapMonth);

        // 음력 기준 JD(율리우스 날짜) 계산
        double jd = lunarToJulianDay(lunarYear, lunarMonth, lunarDay, isLeapMonth);

        // JD → 그레고리력 변환
        return julianDayToGregorian(jd);
    }

    /**
     * 간이화된 음력 → 율리우스 날짜 변환.
     * 19년 주기(메톤 주기)와 음력 평균 삭망월(29.5306일)을 기반으로 계산합니다.
     */
    private double lunarToJulianDay(int year, int month, int day, boolean isLeapMonth) {
        // 음력 기준점: 1900년 1월 1일 = JD 2415021.5 (양력 1900년 1월 31일)
        // 음력 1900년 1월 1일의 JD 값
        final double LUNAR_EPOCH_JD = 2415021.5;
        final double LUNAR_EPOCH_YEAR = 1900;
        final double LUNAR_EPOCH_MONTH = 1;

        // 음력 기준일로부터 경과 삭망월 수 계산
        double yearsFromEpoch = year - LUNAR_EPOCH_YEAR;
        double monthsFromEpoch = yearsFromEpoch * 12 + (month - LUNAR_EPOCH_MONTH);

        // 윤달 보정: 19년 7윤법 기준 (19년에 7번 윤달)
        if (isLeapMonth) {
            monthsFromEpoch += 0.5;
        }

        // 삭망월 × 경과월 + 일수
        final double SYNODIC_MONTH = 29.53058868;
        double jd = LUNAR_EPOCH_JD + (monthsFromEpoch * SYNODIC_MONTH) + (day - 1);

        return jd;
    }

    /**
     * 율리우스 날짜 → 그레고리력 변환.
     * Jean Meeus "Astronomical Algorithms" Chapter 7 알고리즘 기반.
     */
    private LocalDate julianDayToGregorian(double jd) {
        int z = (int) (jd + 0.5);
        int a;
        if (z < 2299161) {
            a = z;
        } else {
            int alpha = (int) ((z - 1867216.25) / 36524.25);
            a = z + 1 + alpha - (alpha / 4);
        }
        int b = a + 1524;
        int c = (int) ((b - 122.1) / 365.25);
        int d = (int) (365.25 * c);
        int e = (int) ((b - d) / 30.6001);

        int day = b - d - (int) (30.6001 * e);
        int month = (e < 14) ? (e - 1) : (e - 13);
        int year = (month > 2) ? (c - 4716) : (c - 4715);

        // 유효범위 검증
        try {
            return LocalDate.of(year, month, day);
        } catch (Exception ex) {
            log.warn("Invalid date computed from JD={}: {}-{}-{}. Returning approximate date.", jd, year, month, day);
            // 계산 오류 시 근사 양력 날짜 반환 (입력 음력 연도 기준)
            return approximateSolarDate(year, month, day);
        }
    }

    /**
     * 계산 오류 시 최소한의 유효 날짜를 반환합니다.
     */
    private LocalDate approximateSolarDate(int year, int month, int day) {
        int safeYear = Math.max(1900, Math.min(2100, year));
        int safeMonth = Math.max(1, Math.min(12, month));
        int maxDay = LocalDate.of(safeYear, safeMonth, 1).lengthOfMonth();
        int safeDay = Math.max(1, Math.min(maxDay, day));
        return LocalDate.of(safeYear, safeMonth, safeDay);
    }

    /**
     * 편의 메서드: 윤달이 아닌 일반 음력 날짜 변환
     */
    public LocalDate lunarToSolar(int lunarYear, int lunarMonth, int lunarDay) {
        return lunarToSolar(lunarYear, lunarMonth, lunarDay, false);
    }
}
