package com.capstone.sajurecommender.features.recommend.controller;

import com.capstone.sajurecommender.common.dto.ApiResponse;
import com.capstone.sajurecommender.features.recommend.dto.RecommendRequest;
import com.capstone.sajurecommender.features.recommend.dto.RecommendResponse;
import com.capstone.sajurecommender.features.recommend.dto.RecommendResponse.*;
import com.capstone.sajurecommender.features.recommend.service.RecommendationEngine;
import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.capstone.sajurecommender.features.saju.service.SajuCalculator;
import com.capstone.sajurecommender.features.weather.dto.WeatherResponse;
import com.capstone.sajurecommender.features.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "Recommend", description = "상황 인지 추천 API")
public class RecommendController {

    private final SajuCalculator sajuCalculator;
    private final SajuAnalyzer sajuAnalyzer;
    private final WeatherService weatherService;
    private final RecommendationEngine recommendationEngine;

    @PostMapping
    @Operation(summary = "맞춤 장소 추천", description = "사주 + 기분 + 위치를 기반으로 최적의 장소와 아이템을 추천합니다.")
    public ApiResponse<RecommendResponse> recommend(@Valid @RequestBody RecommendRequest request) {
        log.info("Recommendation request: mood={}, location=({},{})", 
                request.getMood(), request.getLat(), request.getLon());

        // 1. Calculate saju
        var sajuInput = request.getSajuInput();
        LocalDateTime birthDateTime = LocalDateTime.of(
                sajuInput.getYear(), sajuInput.getMonth(), sajuInput.getDay(),
                sajuInput.getHour(), 0
        );
        FourPillars pillars = sajuCalculator.calculate(birthDateTime);

        // 2. Get weather
        WeatherResponse weather = weatherService.getCurrentWeather(request.getLat(), request.getLon());

        // 3. Generate recommendations
        List<PlaceRecommendation> recommendations = recommendationEngine.recommend(
                pillars, request.getMood(), weather, request.getCategory(), 10
        );

        // 4. Build context summary
        Element neededElement = sajuAnalyzer.getNeededElement(pillars);
        int fortuneScore = sajuAnalyzer.calculateDailyFortune(pillars, LocalDate.now());

        String timeOfDay = getTimeOfDay(LocalTime.now().getHour());

        RecommendResponse response = RecommendResponse.builder()
                .mood(request.getMood())
                .moodEmoji(recommendationEngine.getMoodEmoji(request.getMood()))
                .weather(WeatherInfo.builder()
                        .main(weather.getMain())
                        .description(weather.getDescription())
                        .temperature(weather.getTemperature())
                        .elementMapping(weather.getElementMapping())
                        .elementColor(weather.getElementColor())
                        .build())
                .recommendations(recommendations)
                .contextSummary(ContextSummary.builder()
                        .neededElement(neededElement.getKorean())
                        .neededElementColor(neededElement.getColor())
                        .weatherElement(weather.getElementMapping())
                        .timeOfDay(timeOfDay)
                        .fortuneScore(fortuneScore)
                        .build())
                .build();

        return ApiResponse.ok(response);
    }

    private String getTimeOfDay(int hour) {
        if (hour >= 5 && hour < 9) return "이른 아침";
        if (hour >= 9 && hour < 12) return "오전";
        if (hour >= 12 && hour < 14) return "점심";
        if (hour >= 14 && hour < 17) return "오후";
        if (hour >= 17 && hour < 20) return "저녁";
        if (hour >= 20 && hour < 23) return "밤";
        return "심야";
    }
}
