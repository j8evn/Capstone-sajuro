package com.capstone.sajurecommender.features.saju.controller;

import com.capstone.sajurecommender.common.dto.ApiResponse;
import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.Pillar;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;
import com.capstone.sajurecommender.features.saju.dto.SajuProfileResponse;
import com.capstone.sajurecommender.features.saju.dto.SajuProfileResponse.*;
import com.capstone.sajurecommender.features.saju.dto.SajuRequest;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.capstone.sajurecommender.features.saju.service.SajuCalculator;
import com.capstone.sajurecommender.features.ai.service.GeminiAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final GeminiAiService geminiAiService;

    @PostMapping("/calculate")
    @Operation(summary = "사주팔자 계산", description = "생년월일시를 입력받아 사주팔자를 계산하고 분석 결과를 반환합니다.")
    public ApiResponse<SajuProfileResponse> calculate(@Valid @RequestBody SajuRequest request) {
        log.info("Calculating saju for: {}-{}-{} {}시", request.getYear(), request.getMonth(), request.getDay(), request.getHour());

        LocalDateTime birthDateTime = LocalDateTime.of(
                request.getYear(), request.getMonth(), request.getDay(),
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
