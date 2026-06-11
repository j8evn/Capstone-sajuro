package com.capstone.sajurecommender.features.recommend.service;

import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.capstone.sajurecommender.features.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Context Engine: 사주 프로파일 + 현재 날씨 + 시간대 + 기분을 통합하여
 * 상황 벡터(ContextVector)를 생성합니다.
 *
 * 이 벡터는 RecommendationEngine의 점수 산출에 사용됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContextEngine {

    private final SajuAnalyzer sajuAnalyzer;

    /**
     * 현재 상황을 종합한 컨텍스트 벡터를 생성합니다.
     */
    public ContextVector buildContext(FourPillars pillars, String mood, WeatherResponse weather) {
        Element neededElement = sajuAnalyzer.getNeededElement(pillars);
        Element dayMasterElement = pillars.dayMaster().getElement();
        int fortuneScore = sajuAnalyzer.calculateDailyFortune(pillars, LocalDate.now());

        int hour = LocalTime.now().getHour();
        String timeOfDay = classifyTimeOfDay(hour);
        boolean isWeekend = LocalDate.now().getDayOfWeek().getValue() >= 6;
        int congestionLevel = estimateCongestionLevel(hour, isWeekend);

        String weatherElementKorean = weather.getElementMapping();

        log.debug("Context: neededElement={}, mood={}, weather={}, time={}, fortune={}",
                neededElement.getKorean(), mood, weatherElementKorean, timeOfDay, fortuneScore);

        return new ContextVector(
                neededElement,
                dayMasterElement,
                fortuneScore,
                mood,
                weatherElementKorean,
                weather.getOutdoorScore(),
                hour,
                timeOfDay,
                isWeekend,
                congestionLevel
        );
    }

    /**
     * 시간대 분류.
     */
    public String classifyTimeOfDay(int hour) {
        if (hour >= 5 && hour < 9) return "이른 아침";
        if (hour >= 9 && hour < 12) return "오전";
        if (hour >= 12 && hour < 14) return "점심";
        if (hour >= 14 && hour < 17) return "오후";
        if (hour >= 17 && hour < 20) return "저녁";
        if (hour >= 20 && hour < 23) return "밤";
        return "심야";
    }

    /**
     * 시간대 기반 추정 혼잡도 (0~100, 낮을수록 혼잡).
     * 실시간 혼잡도 API 대신 시간대·요일 기반 mock 추정값을 사용합니다.
     */
    public int estimateCongestionLevel(int hour, boolean isWeekend) {
        int base = 50;

        // 피크 타임 가중치
        if ((hour >= 12 && hour <= 13) || (hour >= 18 && hour <= 19)) {
            base += 25;
        } else if (hour >= 9 && hour <= 11) {
            base += 10;
        } else if (hour < 7 || hour >= 22) {
            base -= 30;
        }

        if (isWeekend) base += 15;

        return Math.max(0, Math.min(100, base));
    }

    /**
     * 상황 벡터 — 모든 컨텍스트 정보를 담는 불변 레코드.
     */
    public record ContextVector(
            Element neededElement,          // 용신 (보충 필요 오행)
            Element dayMasterElement,       // 일간 오행
            int fortuneScore,               // 오늘의 운세 점수 (0~100)
            String mood,                    // 현재 기분
            String weatherElementKorean,    // 날씨 오행 (한글)
            int outdoorScore,               // 야외 활동 적합도 (0~100)
            int currentHour,                // 현재 시각 (0~23)
            String timeOfDay,               // 시간대 레이블
            boolean isWeekend,              // 주말 여부
            int congestionLevel             // 추정 혼잡도 (0~100, 높을수록 혼잡)
    ) {}
}
