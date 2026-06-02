package com.capstone.sajurecommender.features.weather.controller;

import com.capstone.sajurecommender.common.dto.ApiResponse;
import com.capstone.sajurecommender.features.weather.dto.WeatherResponse;
import com.capstone.sajurecommender.features.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "날씨 조회 API")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    @Operation(summary = "현재 날씨 조회", description = "위도/경도 기반으로 현재 날씨와 오행 매핑 정보를 반환합니다.")
    public ApiResponse<WeatherResponse> getWeather(
            @RequestParam(defaultValue = "37.5665") double lat,
            @RequestParam(defaultValue = "126.978") double lon) {
        WeatherResponse weather = weatherService.getCurrentWeather(lat, lon);
        return ApiResponse.ok(weather);
    }
}
