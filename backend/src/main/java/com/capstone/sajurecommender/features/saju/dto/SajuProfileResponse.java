package com.capstone.sajurecommender.features.saju.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SajuProfileResponse {

    private BirthInfo birthInfo;
    private PillarInfo fourPillars;
    private AnalysisResult analysis;
    private MarketingVariables marketingVariables;
    private DailyFortune dailyFortune;
    private boolean aiEnabled;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BirthInfo {
        private int year;
        private int month;
        private int day;
        private int hour;
        private String calendarType;
        private String gender;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PillarInfo {
        private PillarDetail year;
        private PillarDetail month;
        private PillarDetail day;
        private PillarDetail hour;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PillarDetail {
        private String stemKorean;
        private String stemHanja;
        private String branchKorean;
        private String branchHanja;
        private String stemElement;
        private String branchElement;
        private String stemElementColor;
        private String branchElementColor;
        private String yinYang;
        private String animal; // only for year/hour branches
        private String display; // e.g., "갑자(甲子)"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResult {
        private String dayMaster;
        private String dayMasterElement;
        private String dayMasterElementColor;
        private Map<String, Integer> elementDistribution;
        private Map<String, Integer> yinYangBalance;
        private String neededElement;
        private String neededElementColor;
        private List<String> personalityKeywords;
        private String socialTendency;
        private String decisionStyle;
        private String personalityDescription; // AI-generated
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketingVariables {
        private List<String> preferredColors;
        private List<String> preferredFoods;
        private List<String> preferredActivities;
        private String luckyDirection;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyFortune {
        private int score;
        private String description;
        private String aiDescription; // AI-generated detailed fortune
        private String date;
    }
}
