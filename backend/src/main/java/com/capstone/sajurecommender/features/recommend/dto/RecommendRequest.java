package com.capstone.sajurecommender.features.recommend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {

    @NotNull(message = "사주 프로파일이 필요합니다")
    private SajuInput sajuInput;

    @NotNull(message = "기분을 선택해주세요")
    private String mood; // happy, sad, angry, calm, thoughtful, excited

    @Builder.Default
    private double lat = 37.5665;

    @Builder.Default
    private double lon = 126.978;

    private String category; // Optional: filter by category (cafe, restaurant, park, culture, shopping)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SajuInput {
        private int year;
        private int month;
        private int day;
        private int hour;
        private String calendarType;
        private String gender;
    }
}
