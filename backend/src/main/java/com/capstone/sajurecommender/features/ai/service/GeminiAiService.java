package com.capstone.sajurecommender.features.ai.service;

import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import com.capstone.sajurecommender.features.saju.service.SajuAnalyzer;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI-powered saju interpretation and recommendation text generation
 * using Google Gemini API.
 *
 * Falls back to template-based text when API key is not configured ("none").
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiService {

    private final SajuAnalyzer sajuAnalyzer;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    private Client geminiClient;
    private boolean aiEnabled = false;

    @PostConstruct
    public void init() {
        if (apiKey != null && !apiKey.isBlank() && !"none".equals(apiKey)) {
            try {
                geminiClient = Client.builder().apiKey(apiKey).build();
                aiEnabled = true;
                log.info("✅ Gemini AI enabled with model: {}", model);
            } catch (Exception e) {
                log.warn("⚠️ Gemini AI init failed, using template fallback: {}", e.getMessage());
                aiEnabled = false;
            }
        } else {
            log.info("ℹ️ Gemini AI disabled (no API key). Set GEMINI_API_KEY env variable to enable.");
        }
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    // ==================== 사주 해석 ====================

    /**
     * Generate a comprehensive AI-powered saju interpretation.
     */
    public String generateSajuInterpretation(FourPillars pillars, int fortuneScore) {
        if (!aiEnabled) {
            return sajuAnalyzer.getDailyFortuneDescription(fortuneScore);
        }

        String prompt = buildSajuPrompt(pillars, fortuneScore);

        try {
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, null);
            String text = response.text();
            log.info("Gemini saju interpretation generated ({} chars)", text.length());
            return text.trim();
        } catch (Exception e) {
            log.warn("Gemini API call failed for saju interpretation: {}", e.getMessage());
            return sajuAnalyzer.getDailyFortuneDescription(fortuneScore);
        }
    }

    /**
     * Generate a personality description based on saju analysis.
     */
    public String generatePersonalityDescription(FourPillars pillars, List<String> keywords) {
        if (!aiEnabled) {
            return "당신은 " + String.join(", ", keywords) + " 성향을 가지고 있습니다.";
        }

        Element dayMasterElement = pillars.dayMaster().getElement();
        Map<Element, Integer> dist = pillars.elementDistribution();

        String prompt = String.format("""
            당신은 사주명리학 전문가입니다. 아래 사주 정보를 바탕으로 이 사람의 성격과 성향을 
            친근하고 긍정적인 톤으로 3-4문장으로 설명해주세요. 반말은 사용하지 마세요.
            
            일간(나의 본질): %s (%s)
            성격 키워드: %s
            오행 분포: 목=%d, 화=%d, 토=%d, 금=%d, 수=%d
            
            주의: 부정적인 표현은 피하고, 강점 위주로 설명해주세요. 이모지를 1-2개 사용해도 좋습니다.
            """,
                pillars.dayMaster().getKorean() + "(" + pillars.dayMaster().getHanja() + ")",
                dayMasterElement.getKorean(),
                String.join(", ", keywords),
                dist.getOrDefault(Element.WOOD, 0),
                dist.getOrDefault(Element.FIRE, 0),
                dist.getOrDefault(Element.EARTH, 0),
                dist.getOrDefault(Element.METAL, 0),
                dist.getOrDefault(Element.WATER, 0)
        );

        try {
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, null);
            return response.text().trim();
        } catch (Exception e) {
            log.warn("Gemini personality generation failed: {}", e.getMessage());
            return "당신은 " + String.join(", ", keywords) + " 성향을 가지고 있습니다.";
        }
    }

    // ==================== 추천 이유 ====================

    /**
     * Generate an AI-powered recommendation reason text.
     */
    public String generateRecommendationReason(
            String placeName,
            String placeCategory,
            String placeDescription,
            Element neededElement,
            Element placeElement,
            String mood,
            int matchScore) {

        if (!aiEnabled) {
            return null; // Null means: use the existing template-based reason
        }

        String moodKorean = switch (mood) {
            case "happy" -> "행복한";
            case "sad" -> "우울한";
            case "angry" -> "짜증나는";
            case "calm" -> "평온한";
            case "thoughtful" -> "생각이 많은";
            case "excited" -> "신나는";
            default -> mood;
        };

        String prompt = String.format("""
            사주명리학 기반 장소 추천 시스템입니다. 아래 정보를 바탕으로 이 장소를 
            추천하는 이유를 2문장으로 작성해주세요. 사주의 오행 관점을 포함해주세요.
            
            장소: %s (%s)
            장소 설명: %s
            사용자에게 필요한 오행: %s
            이 장소의 오행 속성: %s
            현재 기분: %s
            매칭 점수: %d/100
            
            주의: 
            - 자연스럽고 친근한 톤으로 작성
            - 오행의 기운이 어떻게 도움이 되는지 간단히 설명
            - 이모지 1개 사용
            - 2문장 이내로 간결하게
            """,
                placeName, placeCategory, placeDescription,
                neededElement.getKorean() + "(" + neededElement.getHanja() + ")",
                placeElement.getKorean() + "(" + placeElement.getHanja() + ")",
                moodKorean,
                matchScore
        );

        try {
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, null);
            String text = response.text().trim();
            log.debug("AI recommendation reason generated for {}: {}", placeName, text);
            return text;
        } catch (Exception e) {
            log.warn("Gemini recommendation reason failed for {}: {}", placeName, e.getMessage());
            return null;
        }
    }

    // ==================== 오늘의 운세 상세 ====================

    /**
     * Generate a detailed daily fortune text with AI.
     */
    public String generateDailyFortune(FourPillars pillars, int score, LocalDate today) {
        if (!aiEnabled) {
            return sajuAnalyzer.getDailyFortuneDescription(score);
        }

        String prompt = buildDailyFortunePrompt(pillars, score, today);

        try {
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, null);
            return response.text().trim();
        } catch (Exception e) {
            log.warn("Gemini daily fortune failed: {}", e.getMessage());
            return sajuAnalyzer.getDailyFortuneDescription(score);
        }
    }

    // ==================== Private Helpers ====================

    private String buildSajuPrompt(FourPillars pillars, int fortuneScore) {
        Element needed = sajuAnalyzer.getNeededElement(pillars);
        Map<Element, Integer> dist = pillars.elementDistribution();

        return String.format("""
            당신은 사주명리학 전문 상담사입니다. 다음 사주 정보를 분석하여 
            오늘의 운세를 4-5문장으로 자연스럽게 설명해주세요.
            
            사주팔자:
            - 년주: %s
            - 월주: %s
            - 일주: %s
            - 시주: %s
            
            일간(나): %s (%s)
            오행 분포: 목=%d, 화=%d, 토=%d, 금=%d, 수=%d
            보충 필요 오행: %s
            오늘의 운세 점수: %d/100
            
            규칙:
            - 친근하고 따뜻한 톤으로 작성
            - 구체적인 행동 조언을 1-2가지 포함
            - 보충이 필요한 오행과 관련된 활동 제안
            - 부정적인 내용도 긍정적으로 표현
            - 이모지 2-3개 사용
            - 5문장 이내
            """,
                pillars.yearPillar().toKoreanString(),
                pillars.monthPillar().toKoreanString(),
                pillars.dayPillar().toKoreanString(),
                pillars.hourPillar().toKoreanString(),
                pillars.dayMaster().getKorean(),
                pillars.dayMaster().getElement().getKorean(),
                dist.getOrDefault(Element.WOOD, 0),
                dist.getOrDefault(Element.FIRE, 0),
                dist.getOrDefault(Element.EARTH, 0),
                dist.getOrDefault(Element.METAL, 0),
                dist.getOrDefault(Element.WATER, 0),
                needed.getKorean(),
                fortuneScore
        );
    }

    private String buildDailyFortunePrompt(FourPillars pillars, int score, LocalDate today) {
        Element needed = sajuAnalyzer.getNeededElement(pillars);

        return String.format("""
            사주명리학 전문가로서, 아래 사주와 오늘 날짜를 바탕으로 
            오늘의 운세를 3문장으로 작성해주세요.
            
            일간: %s (%s)
            필요한 오행: %s
            오늘 날짜: %s (%s)
            운세 점수: %d/100
            
            규칙:
            - 따뜻하고 격려하는 톤
            - 구체적 행동 조언 1가지
            - 3문장 이내, 이모지 1개
            """,
                pillars.dayMaster().getKorean(),
                pillars.dayMaster().getElement().getKorean(),
                needed.getKorean(),
                today.toString(),
                today.getDayOfWeek().toString(),
                score
        );
    }
}
