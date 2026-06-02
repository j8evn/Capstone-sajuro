package com.capstone.sajurecommender.features.weather.service;

import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import com.capstone.sajurecommender.features.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient weatherWebClient;

    @Value("${app.weather.api-key}")
    private String apiKey;

    /**
     * Get current weather data for given coordinates.
     * Falls back to mock data if API key is "mock" or API call fails.
     */
    public WeatherResponse getCurrentWeather(double lat, double lon) {
        if ("mock".equals(apiKey)) {
            log.info("Using mock weather data (no API key configured)");
            return generateMockWeather(lat, lon);
        }

        try {
            Map<String, Object> response = weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "kr")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseWeatherResponse(response);
        } catch (Exception e) {
            log.warn("Weather API call failed, using mock data: {}", e.getMessage());
            return generateMockWeather(lat, lon);
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherResponse parseWeatherResponse(Map<String, Object> response) {
        Map<String, Object> main = (Map<String, Object>) response.get("main");
        Map<String, Object> wind = (Map<String, Object>) response.get("wind");
        var weatherList = (java.util.List<Map<String, Object>>) response.get("weather");
        Map<String, Object> weather = weatherList.get(0);

        String weatherMain = (String) weather.get("main");
        double temp = ((Number) main.get("temp")).doubleValue();
        int humidity = ((Number) main.get("humidity")).intValue();

        Element element = mapWeatherToElement(weatherMain);
        int outdoorScore = calculateOutdoorScore(temp, humidity, weatherMain);

        return WeatherResponse.builder()
                .city((String) response.get("name"))
                .temperature(temp)
                .feelsLike(((Number) main.get("feels_like")).doubleValue())
                .humidity(humidity)
                .windSpeed(((Number) wind.get("speed")).doubleValue())
                .main(weatherMain)
                .description((String) weather.get("description"))
                .icon((String) weather.get("icon"))
                .elementMapping(element.getKorean())
                .elementColor(element.getColor())
                .outdoorScore(outdoorScore)
                .build();
    }

    /**
     * Map weather condition to Five Elements (오행).
     */
    private Element mapWeatherToElement(String weatherMain) {
        return switch (weatherMain.toLowerCase()) {
            case "clear", "sunny" -> Element.FIRE;
            case "rain", "drizzle", "thunderstorm" -> Element.WATER;
            case "clouds", "mist", "fog", "haze" -> Element.METAL;
            case "snow" -> Element.WATER;
            default -> Element.EARTH;
        };
    }

    /**
     * Calculate outdoor suitability score.
     */
    private int calculateOutdoorScore(double temp, int humidity, String weatherMain) {
        int score = 70;

        // Temperature factor (ideal: 18-25°C)
        if (temp >= 18 && temp <= 25) score += 15;
        else if (temp >= 10 && temp <= 30) score += 5;
        else if (temp < 0 || temp > 35) score -= 20;
        else score -= 10;

        // Humidity factor
        if (humidity > 80) score -= 10;
        if (humidity < 30) score -= 5;

        // Weather condition
        score += switch (weatherMain.toLowerCase()) {
            case "clear" -> 15;
            case "clouds" -> 5;
            case "rain", "drizzle" -> -20;
            case "thunderstorm" -> -30;
            case "snow" -> -15;
            default -> 0;
        };

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Generate realistic mock weather data based on time of day.
     */
    private WeatherResponse generateMockWeather(double lat, double lon) {
        int hour = LocalTime.now().getHour();
        Random random = new Random(Long.hashCode((long) (lat * 1000 + lon * 100 + hour)));

        // Simulate seasonal weather for Seoul area
        String[] conditions = {"Clear", "Clouds", "Clear", "Clouds", "Rain"};
        String condition = conditions[random.nextInt(conditions.length)];

        double baseTemp = 22 + (random.nextDouble() * 6 - 3);
        if (hour < 6 || hour > 20) baseTemp -= 4;

        Element element = mapWeatherToElement(condition);
        int outdoorScore = calculateOutdoorScore(baseTemp, 55, condition);

        String description = switch (condition) {
            case "Clear" -> "맑음";
            case "Clouds" -> "구름 조금";
            case "Rain" -> "가벼운 비";
            default -> "보통";
        };

        return WeatherResponse.builder()
                .city("서울")
                .temperature(Math.round(baseTemp * 10.0) / 10.0)
                .feelsLike(Math.round((baseTemp - 1.5) * 10.0) / 10.0)
                .humidity(50 + random.nextInt(30))
                .windSpeed(1.0 + random.nextDouble() * 3)
                .main(condition)
                .description(description)
                .icon(condition.equals("Clear") ? "01d" : condition.equals("Clouds") ? "03d" : "10d")
                .elementMapping(element.getKorean())
                .elementColor(element.getColor())
                .outdoorScore(outdoorScore)
                .build();
    }
}
