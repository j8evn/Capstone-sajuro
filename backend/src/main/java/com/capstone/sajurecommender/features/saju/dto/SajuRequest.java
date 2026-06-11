package com.capstone.sajurecommender.features.saju.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SajuRequest {

    @NotNull(message = "출생 연도는 필수입니다")
    @Min(value = 1900, message = "1900년 이후만 지원합니다")
    @Max(value = 2100, message = "2100년 이전만 지원합니다")
    private Integer year;

    @NotNull(message = "출생 월은 필수입니다")
    @Min(value = 1, message = "월은 1~12 사이여야 합니다")
    @Max(value = 12, message = "월은 1~12 사이여야 합니다")
    private Integer month;

    @NotNull(message = "출생 일은 필수입니다")
    @Min(value = 1, message = "일은 1~31 사이여야 합니다")
    @Max(value = 31, message = "일은 1~31 사이여야 합니다")
    private Integer day;

    @NotNull(message = "출생 시간은 필수입니다")
    @Min(value = 0, message = "시간은 0~23 사이여야 합니다")
    @Max(value = 23, message = "시간은 0~23 사이여야 합니다")
    private Integer hour;

    @Builder.Default
    private String calendarType = "solar"; // solar or lunar

    @Builder.Default
    private boolean leapMonth = false; // 윤달 여부 (음력 입력 시에만 사용)

    private String gender; // male or female

    public boolean isLunar() {
        return "lunar".equalsIgnoreCase(calendarType);
    }
}

