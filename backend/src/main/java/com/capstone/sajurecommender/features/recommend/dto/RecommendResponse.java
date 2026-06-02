package com.capstone.sajurecommender.features.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponse {

    private String mood;
    private String moodEmoji;
    private WeatherInfo weather;
    private List<PlaceRecommendation> recommendations;
    private ContextSummary contextSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfo {
        private String main;
        private String description;
        private double temperature;
        private String elementMapping;
        private String elementColor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceRecommendation {
        private String id;
        private String name;
        private String category;
        private String categoryEmoji;
        private String description;
        private String address;
        private double lat;
        private double lon;
        private String atmosphere;
        private int priceRange;
        private String imageUrl;
        private int matchScore;          // 0-100 overall match score
        private ScoreBreakdown scoreBreakdown;
        private String reasonText;       // Human-readable recommendation reason
        private List<String> matchedTags;
        private List<MenuItemInfo> menuItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreBreakdown {
        private int sajuScore;       // 사주 적합도
        private int weatherScore;    // 날씨 적합도
        private int moodScore;       // 기분 적합도
        private int timeScore;       // 시간 적합도
        private int congestionScore; // 혼잡도 점수
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemInfo {
        private String name;
        private String description;
        private int price;
        private String element;
        private String elementColor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextSummary {
        private String neededElement;
        private String neededElementColor;
        private String weatherElement;
        private String timeOfDay;
        private int fortuneScore;
    }
}
