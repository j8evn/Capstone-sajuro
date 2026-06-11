package com.capstone.sajurecommender.features.saju.controller;

import com.capstone.sajurecommender.common.dto.ApiResponse;
import com.capstone.sajurecommender.common.exception.ApiException;
import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.Pillar;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;
import com.capstone.sajurecommender.features.saju.dto.SajuProfileResponse;
import com.capstone.sajurecommender.features.saju.dto.SajuProfileResponse.*;
import com.capstone.sajurecommender.features.saju.dto.SajuRequest;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.capstone.sajurecommender.features.saju.service.SajuCalculator;
import com.capstone.sajurecommender.features.saju.service.LunarCalendarConverter;
import com.capstone.sajurecommender.features.ai.service.GeminiAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/saju")
@RequiredArgsConstructor
@Tag(name = "Saju", description = "사주 계산 및 분석 API")
public class SajuController {

    private final SajuCalculator calculator;
    private final SajuAnalyzer analyzer;
    private final LunarCalendarConverter lunarConverter;
    private final GeminiAiService geminiAiService;

    @PostMapping("/calculate")
    @Operation(summary = "사주팔자 계산", description = "생년월일시를 입력받아 사주팔자를 계산하고 분석 결과를 반환합니다. 음력 입력 시 자동으로 양력으로 변환합니다.")
    public ApiResponse<SajuProfileResponse> calculate(@Valid @RequestBody SajuRequest request) {
        log.info("Calculating saju for: {}-{}-{} {}시 ({}{})",
                request.getYear(), request.getMonth(), request.getDay(), request.getHour(),
                request.getCalendarType(), request.isLeapMonth() ? " 윤달" : "");

        // 음력 입력 처리: 양력으로 변환
        LocalDate solarDate;
        if (request.isLunar()) {
            try {
                solarDate = lunarConverter.lunarToSolar(
                        request.getYear(), request.getMonth(), request.getDay(), request.isLeapMonth());
                log.info("Lunar {} converted to Solar {}", 
                        request.getYear() + "-" + request.getMonth() + "-" + request.getDay(),
                        solarDate);
            } catch (Exception e) {
                log.warn("Lunar conversion failed, using input as solar: {}", e.getMessage());
                throw ApiException.badRequest("음력 날짜 변환에 실패했습니다: " + e.getMessage());
            }
        } else {
            try {
                solarDate = LocalDate.of(request.getYear(), request.getMonth(), request.getDay());
            } catch (Exception e) {
                throw ApiException.badRequest("유효하지 않은 날짜입니다: " + request.getYear() + "-" + request.getMonth() + "-" + request.getDay());
            }
        }

        LocalDateTime birthDateTime = LocalDateTime.of(
                solarDate.getYear(), solarDate.getMonthValue(), solarDate.getDayOfMonth(),
                request.getHour(), 0
        );

        // Calculate Four Pillars
        FourPillars pillars = calculator.calculate(birthDateTime);

        // Analyze
        Element neededElement = analyzer.getNeededElement(pillars);
        int fortuneScore = analyzer.calculateDailyFortune(pillars, LocalDate.now());

        // AI-generated content
        var personalityKeywords = analyzer.getPersonalityKeywords(pillars);
        String personalityDesc = geminiAiService.generatePersonalityDescription(pillars, personalityKeywords);
        String aiFortuneDesc = geminiAiService.generateDailyFortune(pillars, fortuneScore, LocalDate.now());

        // Build response
        SajuProfileResponse response = SajuProfileResponse.builder()
                .birthInfo(BirthInfo.builder()
                        .year(request.getYear())
                        .month(request.getMonth())
                        .day(request.getDay())
                        .hour(request.getHour())
                        .calendarType(request.getCalendarType())
                        .gender(request.getGender())
                        .build())
                .fourPillars(PillarInfo.builder()
                        .year(toPillarDetail(pillars.yearPillar()))
                        .month(toPillarDetail(pillars.monthPillar()))
                        .day(toPillarDetail(pillars.dayPillar()))
                        .hour(toPillarDetail(pillars.hourPillar()))
                        .build())
                .analysis(AnalysisResult.builder()
                        .dayMaster(pillars.dayMaster().getKorean() + "(" + pillars.dayMaster().getHanja() + ")")
                        .dayMasterElement(pillars.dayMaster().getElement().getKorean())
                        .dayMasterElementColor(pillars.dayMaster().getElement().getColor())
                        .elementDistribution(pillars.elementDistribution().entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey().getKorean(), Map.Entry::getValue)))
                        .yinYangBalance(pillars.yinYangBalance().entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey().getKorean(), Map.Entry::getValue)))
                        .neededElement(neededElement.getKorean())
                        .neededElementColor(neededElement.getColor())
                        .personalityKeywords(personalityKeywords)
                        .socialTendency(analyzer.getSocialTendency(pillars))
                        .decisionStyle(analyzer.getDecisionStyle(pillars))
                        .personalityDescription(personalityDesc)
                        .build())
                .marketingVariables(MarketingVariables.builder()
                        .preferredColors(analyzer.getLuckyColors(pillars))
                        .preferredFoods(analyzer.getRecommendedFoods(pillars))
                        .preferredActivities(analyzer.getRecommendedActivities(pillars))
                        .luckyDirection(analyzer.getLuckyDirection(pillars))
                        .build())
                .dailyFortune(DailyFortune.builder()
                        .score(fortuneScore)
                        .description(analyzer.getDailyFortuneDescription(fortuneScore))
                        .aiDescription(aiFortuneDesc)
                        .date(LocalDate.now().toString())
                        .build())
                .aiEnabled(geminiAiService.isAiEnabled())
                .build();

        return ApiResponse.ok(response);
    }

    // ==================== /daily-fortune 별도 엔드포인트 ====================

    /**
     * 오늘의 운세 요청 DTO (사주 프로파일 기반)
     */
    @Data
    public static class DailyFortuneRequest {
        @Valid
        private SajuRequest sajuInput;
        private String targetDate; // "2025-06-10" 형식, null이면 오늘
    }

    /**
     * 오늘의 운세 응답 DTO
     */
    @lombok.Builder
    @Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DailyFortuneResponse {
        private int score;
        private String description;
        private String aiDescription;
        private String date;
        private String neededElement;
        private String neededElementColor;
        private List<String> luckyColors;
        private List<String> luckyFoods;
        private List<String> luckyActivities;
        private String luckyDirection;
    }

    @PostMapping("/daily-fortune")
    @Operation(summary = "오늘의 운세", description = "사주 정보를 기반으로 특정 날짜의 운세를 계산합니다. targetDate를 생략하면 오늘 날짜 기준입니다.")
    public ApiResponse<DailyFortuneResponse> dailyFortune(@Valid @RequestBody DailyFortuneRequest request) {
        SajuRequest sajuInput = request.getSajuInput();
        log.info("Daily fortune request for: {}-{}-{}, targetDate={}",
                sajuInput.getYear(), sajuInput.getMonth(), sajuInput.getDay(), request.getTargetDate());

        // 날짜 결정
        LocalDate targetDate;
        if (request.getTargetDate() != null && !request.getTargetDate().isBlank()) {
            try {
                targetDate = LocalDate.parse(request.getTargetDate());
            } catch (Exception e) {
                throw ApiException.badRequest("날짜 형식이 잘못되었습니다. YYYY-MM-DD 형식을 사용해주세요.");
            }
        } else {
            targetDate = LocalDate.now();
        }

        // 음력 변환 처리
        LocalDate solarDate;
        if (sajuInput.isLunar()) {
            solarDate = lunarConverter.lunarToSolar(
                    sajuInput.getYear(), sajuInput.getMonth(), sajuInput.getDay(), sajuInput.isLeapMonth());
        } else {
            solarDate = LocalDate.of(sajuInput.getYear(), sajuInput.getMonth(), sajuInput.getDay());
        }

        LocalDateTime birthDateTime = LocalDateTime.of(
                solarDate.getYear(), solarDate.getMonthValue(), solarDate.getDayOfMonth(),
                sajuInput.getHour(), 0
        );

        FourPillars pillars = calculator.calculate(birthDateTime);

        int score = analyzer.calculateDailyFortune(pillars, targetDate);
        String description = analyzer.getDailyFortuneDescription(score);
        String aiDescription = geminiAiService.generateDailyFortune(pillars, score, targetDate);

        Element neededElement = analyzer.getNeededElement(pillars);

        DailyFortuneResponse response = DailyFortuneResponse.builder()
                .score(score)
                .description(description)
                .aiDescription(aiDescription)
                .date(targetDate.toString())
                .neededElement(neededElement.getKorean())
                .neededElementColor(neededElement.getColor())
                .luckyColors(analyzer.getLuckyColors(pillars))
                .luckyFoods(analyzer.getRecommendedFoods(pillars))
                .luckyActivities(analyzer.getRecommendedActivities(pillars))
                .luckyDirection(analyzer.getLuckyDirection(pillars))
                .build();

        return ApiResponse.ok(response);
    }

    private PillarDetail toPillarDetail(Pillar pillar) {
        return PillarDetail.builder()
                .stemKorean(pillar.stem().getKorean())
                .stemHanja(pillar.stem().getHanja())
                .branchKorean(pillar.branch().getKorean())
                .branchHanja(pillar.branch().getHanja())
                .stemElement(pillar.stem().getElement().getKorean())
                .branchElement(pillar.branch().getElement().getKorean())
                .stemElementColor(pillar.stem().getElement().getColor())
                .branchElementColor(pillar.branch().getElement().getColor())
                .yinYang(pillar.stem().getYinYang().getKorean())
                .animal(pillar.branch().getAnimal())
                .display(pillar.toKoreanString())
                .build();
    }
}
