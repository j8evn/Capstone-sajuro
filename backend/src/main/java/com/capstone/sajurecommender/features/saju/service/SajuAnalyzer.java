package com.capstone.sajurecommender.features.saju.service;

import com.capstone.sajurecommender.features.saju.domain.FourPillars;
import com.capstone.sajurecommender.features.saju.domain.Pillar;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants;
import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzes calculated Four Pillars data and converts it into
 * marketing-friendly variables and personality profiles.
 */
@Slf4j
@Service
public class SajuAnalyzer {

    // ==================== Element → Marketing Attribute Mappings ====================

    private static final Map<Element, List<String>> ELEMENT_COLORS = Map.of(
            Element.WOOD, List.of("#4CAF50", "#2E7D32", "#81C784"),
            Element.FIRE, List.of("#FF5722", "#D32F2F", "#FF8A65"),
            Element.EARTH, List.of("#FFC107", "#FF8F00", "#FFD54F"),
            Element.METAL, List.of("#9E9E9E", "#E0E0E0", "#BDBDBD"),
            Element.WATER, List.of("#2196F3", "#1565C0", "#64B5F6")
    );

    private static final Map<Element, List<String>> ELEMENT_FOODS = Map.of(
            Element.WOOD, List.of("샐러드", "비빔밥", "녹차", "신맛 음식", "과일주스"),
            Element.FIRE, List.of("매운 떡볶이", "불고기", "커피", "초콜릿", "바비큐"),
            Element.EARTH, List.of("된장찌개", "떡", "고구마 라떼", "곡물빵", "단호박수프"),
            Element.METAL, List.of("삼겹살", "회", "화이트와인", "백김치", "배"),
            Element.WATER, List.of("해물탕", "초밥", "아이스아메리카노", "미역국", "물냉면")
    );

    private static final Map<Element, List<String>> ELEMENT_ACTIVITIES = Map.of(
            Element.WOOD, List.of("공원 산책", "등산", "요가", "식물원 방문", "독서 카페"),
            Element.FIRE, List.of("파티", "콘서트", "매운맛 투어", "놀이공원", "활동적 스포츠"),
            Element.EARTH, List.of("카페 투어", "홈쿠킹 클래스", "도예 체험", "전시회", "맛집 탐방"),
            Element.METAL, List.of("쇼핑", "미술관", "영화 감상", "와인바", "명상"),
            Element.WATER, List.of("수영", "아쿠아리움", "한강 산책", "바다 여행", "스파")
    );

    private static final Map<Element, String> ELEMENT_DIRECTIONS = Map.of(
            Element.WOOD, "동쪽",
            Element.FIRE, "남쪽",
            Element.EARTH, "중앙",
            Element.METAL, "서쪽",
            Element.WATER, "북쪽"
    );

    private static final Map<Element, List<String>> ELEMENT_PERSONALITY = Map.of(
            Element.WOOD, List.of("성장지향적", "인자한", "창의적", "진취적"),
            Element.FIRE, List.of("열정적", "활동적", "리더십", "사교적"),
            Element.EARTH, List.of("안정적", "신뢰감", "포용력", "현실적"),
            Element.METAL, List.of("결단력", "정의감", "깔끔한", "원칙적"),
            Element.WATER, List.of("지혜로운", "유연한", "감성적", "적응력")
    );

    /**
     * Determine which element the person is deficient in and should supplement.
     */
    public Element getDeficientElement(FourPillars pillars) {
        Map<Element, Integer> dist = pillars.elementDistribution();
        return dist.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Element.EARTH);
    }

    /**
     * Get the element that needs to be supplemented (용신 approximation).
     * Simple heuristic: find the weakest element, then return the element that generates it.
     */
    public Element getNeededElement(FourPillars pillars) {
        Element dayMasterElement = pillars.dayMaster().getElement();
        Map<Element, Integer> dist = pillars.elementDistribution();
        int dayMasterCount = dist.getOrDefault(dayMasterElement, 0);

        // If day master is weak (appears < 2 times), need elements that support it
        if (dayMasterCount < 2) {
            // Need: the element that generates the day master (印星)
            for (Element e : Element.values()) {
                if (SajuConstants.getGenerating(e) == dayMasterElement) {
                    return e;
                }
            }
        }
        // If day master is strong, need elements that drain it
        return SajuConstants.getGenerating(dayMasterElement);
    }

    /**
     * Get lucky colors based on needed/deficient elements.
     */
    public List<String> getLuckyColors(FourPillars pillars) {
        Element needed = getNeededElement(pillars);
        return ELEMENT_COLORS.getOrDefault(needed, List.of("#9C27B0"));
    }

    /**
     * Get recommended foods based on needed element.
     */
    public List<String> getRecommendedFoods(FourPillars pillars) {
        Element needed = getNeededElement(pillars);
        return ELEMENT_FOODS.getOrDefault(needed, List.of("균형 잡힌 식단"));
    }

    /**
     * Get recommended activities based on needed element.
     */
    public List<String> getRecommendedActivities(FourPillars pillars) {
        Element needed = getNeededElement(pillars);
        return ELEMENT_ACTIVITIES.getOrDefault(needed, List.of("산책"));
    }

    /**
     * Get lucky direction.
     */
    public String getLuckyDirection(FourPillars pillars) {
        Element needed = getNeededElement(pillars);
        return ELEMENT_DIRECTIONS.getOrDefault(needed, "중앙");
    }

    /**
     * Get personality keywords based on element distribution.
     */
    public List<String> getPersonalityKeywords(FourPillars pillars) {
        Element dayMasterElement = pillars.dayMaster().getElement();
        List<String> keywords = new ArrayList<>(ELEMENT_PERSONALITY.getOrDefault(dayMasterElement, List.of()));

        // Add keywords from the strongest element
        Map<Element, Integer> dist = pillars.elementDistribution();
        Element strongest = dist.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(dayMasterElement);

        if (strongest != dayMasterElement) {
            var extraKeywords = ELEMENT_PERSONALITY.getOrDefault(strongest, List.of());
            keywords.addAll(extraKeywords.subList(0, Math.min(2, extraKeywords.size())));
        }

        return keywords;
    }

    /**
     * Get social tendency based on yin-yang balance.
     */
    public String getSocialTendency(FourPillars pillars) {
        Map<YinYang, Integer> balance = pillars.yinYangBalance();
        int yang = balance.getOrDefault(YinYang.YANG, 0);
        int yin = balance.getOrDefault(YinYang.YIN, 0);

        if (yang - yin >= 2) return "extrovert";
        if (yin - yang >= 2) return "introvert";
        return "ambivert";
    }

    /**
     * Get decision-making style based on day master element.
     */
    public String getDecisionStyle(FourPillars pillars) {
        Element dm = pillars.dayMaster().getElement();
        return switch (dm) {
            case FIRE, WOOD -> "intuitive";
            case METAL, WATER -> "analytical";
            case EARTH -> "balanced";
        };
    }

    /**
     * Calculate daily fortune score (0-100) based on the interaction
     * between the person's saju and today's heavenly stem/earthly branch.
     */
    public int calculateDailyFortune(FourPillars pillars, LocalDate today) {
        // Calculate today's day pillar
        LocalDate ref = LocalDate.of(1900, 1, 1);
        long days = java.time.temporal.ChronoUnit.DAYS.between(ref, today);
        int todayStemIdx = Math.floorMod((int) days, 10);
        int todayBranchIdx = Math.floorMod((int) days, 12);

        HeavenlyStem todayStem = HeavenlyStem.fromIndex(todayStemIdx);
        EarthlyBranch todayBranch = EarthlyBranch.fromIndex(todayBranchIdx);

        int score = 50; // Base score

        Element dayMasterEl = pillars.dayMaster().getElement();
        Element todayEl = todayStem.getElement();
        Element neededEl = getNeededElement(pillars);

        // +20 if today's element matches needed element
        if (todayEl == neededEl) score += 20;

        // +15 if today's element generates the day master
        if (SajuConstants.getGenerating(todayEl) == dayMasterEl) score += 15;

        // -10 if today's element overcomes the day master
        if (SajuConstants.getOvercoming(todayEl) == dayMasterEl) score -= 10;

        // +10 for same element (비견 energy)
        if (todayEl == dayMasterEl) score += 10;

        // Yin-Yang harmony bonus
        if (todayStem.getYinYang() != pillars.dayMaster().getYinYang()) score += 5;

        // Branch interaction
        Element todayBranchEl = todayBranch.getElement();
        if (todayBranchEl == neededEl) score += 10;

        // Clamp to 0-100
        score = Math.max(0, Math.min(100, score));

        // Add some natural variation based on day
        int dayVariation = (int) (Math.sin(days * 0.1) * 8);
        score = Math.max(10, Math.min(95, score + dayVariation));

        return score;
    }

    /**
     * Get Ten Gods analysis for all stems relative to the Day Master.
     */
    public Map<TenGod, Integer> analyzeTenGods(FourPillars pillars) {
        Map<TenGod, Integer> tenGodCount = new EnumMap<>(TenGod.class);
        HeavenlyStem dayMaster = pillars.dayMaster();

        for (Pillar p : List.of(pillars.yearPillar(), pillars.monthPillar(), pillars.hourPillar())) {
            TenGod tg = SajuConstants.getTenGod(dayMaster, p.stem());
            tenGodCount.merge(tg, 1, Integer::sum);
        }

        return tenGodCount;
    }

    /**
     * Generate a comprehensive daily fortune description.
     */
    public String getDailyFortuneDescription(int score) {
        if (score >= 80) return "오늘은 매우 좋은 운세입니다! 새로운 시도에 적극적으로 임해보세요.";
        if (score >= 65) return "전반적으로 좋은 하루입니다. 계획한 일을 추진하기 좋습니다.";
        if (score >= 50) return "무난한 하루입니다. 평소처럼 꾸준히 하면 좋은 결과가 있을 거예요.";
        if (score >= 35) return "조금 조심스러운 하루입니다. 중요한 결정은 미루는 것이 좋겠습니다.";
        return "오늘은 충전이 필요한 날입니다. 무리하지 말고 편안하게 보내세요.";
    }
}
