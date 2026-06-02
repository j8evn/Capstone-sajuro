package com.capstone.sajurecommender.features.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String city;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String main;        // e.g., "Clear", "Rain", "Clouds"
    private String description; // e.g., "clear sky"
    private String icon;
    private String elementMapping; // 오행 매핑 (e.g., "화")
    private String elementColor;
    private int outdoorScore;   // 외출 적합도 0-100
}
