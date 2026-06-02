package com.capstone.sajurecommender.features.recommend.service;

import com.capstone.sajurecommender.features.recommend.data.PlaceData;
import com.capstone.sajurecommender.features.recommend.data.PlaceData.Place;
import com.capstone.sajurecommender.features.recommend.dto.RecommendResponse.*;
import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.capstone.sajurecommender.features.weather.dto.WeatherResponse;
import com.capstone.sajurecommender.features.ai.service.GeminiAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Context-aware recommendation engine that combines saju profile,
 * weather, mood, and time context to score and rank places.
 *
 * Score formula:
 * totalScore = (sajuScore * 0.35) + (weatherScore * 0.20) 
 *            + (moodScore * 0.25) + (timeScore * 0.10) 
 *            + (congestionScore * 0.10)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationEngine {

    private final PlaceData placeData;
    private final SajuAnalyzer sajuAnalyzer;
    private final GeminiAiService geminiAiService;

    private static final double WEIGHT_SAJU = 0.35;
    private static final double WEIGHT_WEATHER = 0.20;
    private static final double WEIGHT_MOOD = 0.25;
    private static final double WEIGHT_TIME = 0.10;
    private static final double WEIGHT_CONGESTION = 0.10;

    private static final Map<String, String> MOOD_EMOJI = Map.of(
            "happy", "😊",
            "sad", "😔",
            "angry", "😤",
            "calm", "😌",
            "thoughtful", "🤔",
            "excited", "🎉"
    );

    private static final Map<String, String> CATEGORY_EMOJI = Map.of(
            "cafe", "☕",
            "restaurant", "🍽️",
            "park", "🌿",
            "culture", "🎨",
            "shopping", "🛍️"
    );

    /**
     * Generate ranked place recommendations based on context.
     */
    public List<PlaceRecommendation> recommend(
            FourPillars pillars,
            String mood,
            WeatherResponse weather,
            String category,
            int maxResults) {

        Element neededElement = sajuAnalyzer.getNeededElement(pillars);
        Element weatherElement = Element.valueOf(
                mapKoreanToElementName(weather.getElementMapping()));

        List<Place> places = placeData.getAllPlaces();

        // Filter by category if specified
        if (category != null && !category.isBlank()) {
            places = places.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        List<PlaceRecommendation> recommendations = places.stream()
                .map(place -> scorePlaceAndBuild(place, pillars, neededElement, weatherElement, mood, weather))
                .sorted(Comparator.comparingInt(PlaceRecommendation::getMatchScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());

        // For top 3 results, try to enhance with AI-generated reason text
        for (int i = 0; i < Math.min(3, recommendations.size()); i++) {
            PlaceRecommendation rec = recommendations.get(i);
            String aiReason = geminiAiService.generateRecommendationReason(
                    rec.getName(), rec.getCategory(), rec.getDescription(),
                    neededElement, Element.valueOf(mapKoreanToElementName(
                            placeData.getAllPlaces().stream()
                                    .filter(p -> p.getId().equals(rec.getId()))
                                    .findFirst()
                                    .map(p -> p.getPrimaryElement().getKorean())
                                    .orElse("토"))),
                    mood, rec.getMatchScore());
            if (aiReason != null) {
                rec.setReasonText(aiReason);
            }
        }

        log.info("Generated {} recommendations for mood={}, neededElement={}, aiEnabled={}",
                recommendations.size(), mood, neededElement.getKorean(), geminiAiService.isAiEnabled());

        return recommendations;
    }

    private PlaceRecommendation scorePlaceAndBuild(
            Place place, FourPillars pillars, Element neededElement,
            Element weatherElement, String mood, WeatherResponse weather) {

        // 1. Saju Score (35%): How well the place's elements match the person's needs
        int sajuScore = calculateSajuScore(place, neededElement, pillars);

        // 2. Weather Score (20%): How suitable the place is for current weather
        int weatherScore = calculateWeatherScore(place, weather, weatherElement);

        // 3. Mood Score (25%): How well the place matches the current mood
        int moodScore = calculateMoodScore(place, mood);

        // 4. Time Score (10%): How appropriate the place is for current time
        int timeScore = calculateTimeScore(place);

        // 5. Congestion Score (10%): Mock congestion estimation
        int congestionScore = calculateCongestionScore(place);

        int totalScore = (int) Math.round(
                sajuScore * WEIGHT_SAJU +
                weatherScore * WEIGHT_WEATHER +
                moodScore * WEIGHT_MOOD +
                timeScore * WEIGHT_TIME +
                congestionScore * WEIGHT_CONGESTION
        );
        totalScore = Math.max(10, Math.min(99, totalScore));

        String reasonText = generateReasonText(place, neededElement, mood, sajuScore, moodScore);

        List<MenuItemInfo> menuInfos = place.getMenuItems().stream()
                .map(mi -> MenuItemInfo.builder()
                        .name(mi.getName())
                        .description(mi.getDescription())
                        .price(mi.getPrice())
                        .element(mi.getElement().getKorean())
                        .elementColor(mi.getElement().getColor())
                        .build())
                .toList();

        return PlaceRecommendation.builder()
                .id(place.getId())
                .name(place.getName())
                .category(place.getCategory())
                .categoryEmoji(CATEGORY_EMOJI.getOrDefault(place.getCategory(), "📍"))
                .description(place.getDescription())
                .address(place.getAddress())
                .lat(place.getLat())
                .lon(place.getLon())
                .atmosphere(place.getAtmosphere())
                .priceRange(place.getPriceRange())
                .imageUrl(place.getImageUrl())
                .matchScore(totalScore)
                .scoreBreakdown(ScoreBreakdown.builder()
                        .sajuScore(sajuScore)
                        .weatherScore(weatherScore)
                        .moodScore(moodScore)
                        .timeScore(timeScore)
                        .congestionScore(congestionScore)
                        .build())
                .reasonText(reasonText)
                .matchedTags(place.getTags())
                .menuItems(menuInfos)
                .build();
    }

    private int calculateSajuScore(Place place, Element neededElement, FourPillars pillars) {
        int score = 50;

        // Direct match: place's primary element matches needed element
        if (place.getPrimaryElement() == neededElement) {
            score += 35;
        }
        // Secondary match
        if (place.getSecondaryElement() == neededElement) {
            score += 20;
        }
        // Generating relationship: place element generates needed element
        if (SajuConstants.getGenerating(place.getPrimaryElement()) == neededElement) {
            score += 15;
        }
        // Penalty: place element overcomes day master's element
        Element dayMasterEl = pillars.dayMaster().getElement();
        if (SajuConstants.getOvercoming(place.getPrimaryElement()) == dayMasterEl) {
            score -= 15;
        }

        return Math.max(0, Math.min(100, score));
    }

    private int calculateWeatherScore(Place place, WeatherResponse weather, Element weatherElement) {
        int score = 60;

        // Outdoor activities get lower scores in bad weather
        if ("park".equals(place.getCategory())) {
            score = weather.getOutdoorScore();
        }

        // Weather element harmony with place element
        if (place.getPrimaryElement() == weatherElement) {
            score += 10;
        }
        if (SajuConstants.getOvercoming(weatherElement) == place.getPrimaryElement()) {
            score -= 10;
        }

        // Indoor places get bonus in bad weather
        if (weather.getOutdoorScore() < 50 && !"park".equals(place.getCategory())) {
            score += 15;
        }

        return Math.max(0, Math.min(100, score));
    }

    private int calculateMoodScore(Place place, String mood) {
        if (place.getSuitableMoods() != null && place.getSuitableMoods().contains(mood)) {
            return 85 + new Random(place.getId().hashCode()).nextInt(15);
        }

        // Partial matches based on mood-atmosphere compatibility
        return switch (mood) {
            case "happy", "excited" -> "lively".equals(place.getAtmosphere()) ? 75 : 45;
            case "sad", "calm" -> "quiet".equals(place.getAtmosphere()) ? 70 : 40;
            case "angry" -> "lively".equals(place.getAtmosphere()) ? 65 : 35;
            case "thoughtful" -> "quiet".equals(place.getAtmosphere()) || "modern".equals(place.getAtmosphere()) ? 70 : 40;
            default -> 50;
        };
    }

    private int calculateTimeScore(Place place) {
        int hour = LocalTime.now().getHour();
        int score = 60;

        // Restaurants score higher around meal times
        if ("restaurant".equals(place.getCategory())) {
            if ((hour >= 11 && hour <= 13) || (hour >= 17 && hour <= 20)) {
                score = 90;
            } else {
                score = 40;
            }
        }

        // Cafes score well in afternoon
        if ("cafe".equals(place.getCategory())) {
            if (hour >= 10 && hour <= 18) score = 85;
            else if (hour >= 8 && hour <= 22) score = 65;
            else score = 30;
        }

        // Parks score well during daytime
        if ("park".equals(place.getCategory())) {
            if (hour >= 7 && hour <= 18) score = 85;
            else score = 30;
        }

        // Shopping is flexible but better during business hours
        if ("shopping".equals(place.getCategory())) {
            if (hour >= 11 && hour <= 21) score = 80;
            else score = 30;
        }

        // Culture venues
        if ("culture".equals(place.getCategory())) {
            if (hour >= 10 && hour <= 18) score = 85;
            else score = 35;
        }

        return score;
    }

    private int calculateCongestionScore(Place place) {
        // Mock congestion based on time of day and category
        int hour = LocalTime.now().getHour();
        int dayOfWeek = java.time.LocalDate.now().getDayOfWeek().getValue();
        boolean isWeekend = dayOfWeek >= 6;

        int congestion = 50; // base: moderate

        // Peak hours
        if ((hour >= 12 && hour <= 13) || (hour >= 18 && hour <= 19)) {
            congestion += 20;
        }
        if (isWeekend) congestion += 15;

        // Popular categories are more congested
        if ("restaurant".equals(place.getCategory()) && hour >= 12 && hour <= 13) {
            congestion += 10;
        }

        // Score inversely: less congestion = higher score
        return Math.max(0, Math.min(100, 100 - congestion));
    }

    private String generateReasonText(Place place, Element neededElement, String mood, int sajuScore, int moodScore) {
        StringBuilder reason = new StringBuilder();

        // Saju-based reason
        if (place.getPrimaryElement() == neededElement) {
            reason.append("당신에게 부족한 '").append(neededElement.getKorean())
                    .append("(").append(neededElement.getHanja()).append(")")
                    .append("' 기운을 보충할 수 있는 공간이에요. ");
        } else if (place.getSecondaryElement() == neededElement) {
            reason.append("이 장소의 '").append(neededElement.getKorean())
                    .append("' 에너지가 당신의 균형을 맞춰줄 거예요. ");
        } else if (SajuConstants.getGenerating(place.getPrimaryElement()) == neededElement) {
            reason.append("이 공간의 '").append(place.getPrimaryElement().getKorean())
                    .append("' 기운이 당신에게 필요한 '").append(neededElement.getKorean())
                    .append("'을 생성해줍니다. ");
        }

        // Mood-based reason
        String moodEmoji = MOOD_EMOJI.getOrDefault(mood, "");
        if (moodScore >= 80) {
            reason.append(moodEmoji).append(" 지금 기분에 딱 맞는 분위기예요!");
        } else if (moodScore >= 60) {
            reason.append(moodEmoji).append(" 현재 기분을 전환하기 좋은 장소입니다.");
        }

        if (reason.isEmpty()) {
            reason.append("오늘의 운세와 현재 상황을 종합적으로 분석한 추천입니다.");
        }

        return reason.toString();
    }

    private String mapKoreanToElementName(String korean) {
        return switch (korean) {
            case "목" -> "WOOD";
            case "화" -> "FIRE";
            case "토" -> "EARTH";
            case "금" -> "METAL";
            case "수" -> "WATER";
            default -> "EARTH";
        };
    }

    public String getMoodEmoji(String mood) {
        return MOOD_EMOJI.getOrDefault(mood, "😐");
    }
}
