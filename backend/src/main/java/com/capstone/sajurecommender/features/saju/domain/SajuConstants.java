package com.capstone.sajurecommender.features.saju.domain;

import java.util.List;
import java.util.Map;

/**
 * Saju (Four Pillars of Destiny) constants including Heavenly Stems, Earthly Branches,
 * Five Elements, Yin-Yang attributes, Hidden Stems, and Ten Gods mappings.
 */
public final class SajuConstants {

    private SajuConstants() {}

    // ==================== 오행 (Five Elements) ====================
    public enum Element {
        WOOD("목", "木", "#4CAF50"),
        FIRE("화", "火", "#FF5722"),
        EARTH("토", "土", "#FFC107"),
        METAL("금", "金", "#9E9E9E"),
        WATER("수", "水", "#2196F3");

        private final String korean;
        private final String hanja;
        private final String color;

        Element(String korean, String hanja, String color) {
            this.korean = korean;
            this.hanja = hanja;
            this.color = color;
        }

        public String getKorean() { return korean; }
        public String getHanja() { return hanja; }
        public String getColor() { return color; }
    }

    // ==================== 음양 (Yin-Yang) ====================
    public enum YinYang {
        YANG("양", "陽"),
        YIN("음", "陰");

        private final String korean;
        private final String hanja;

        YinYang(String korean, String hanja) {
            this.korean = korean;
            this.hanja = hanja;
        }

        public String getKorean() { return korean; }
        public String getHanja() { return hanja; }
    }

    // ==================== 천간 (Heavenly Stems) ====================
    public enum HeavenlyStem {
        GAP("갑", "甲", Element.WOOD, YinYang.YANG, 0),
        EUL("을", "乙", Element.WOOD, YinYang.YIN, 1),
        BYEONG("병", "丙", Element.FIRE, YinYang.YANG, 2),
        JEONG("정", "丁", Element.FIRE, YinYang.YIN, 3),
        MU("무", "戊", Element.EARTH, YinYang.YANG, 4),
        GI("기", "己", Element.EARTH, YinYang.YIN, 5),
        GYEONG("경", "庚", Element.METAL, YinYang.YANG, 6),
        SIN("신", "辛", Element.METAL, YinYang.YIN, 7),
        IM("임", "壬", Element.WATER, YinYang.YANG, 8),
        GYE("계", "癸", Element.WATER, YinYang.YIN, 9);

        private final String korean;
        private final String hanja;
        private final Element element;
        private final YinYang yinYang;
        private final int index;

        HeavenlyStem(String korean, String hanja, Element element, YinYang yinYang, int index) {
            this.korean = korean;
            this.hanja = hanja;
            this.element = element;
            this.yinYang = yinYang;
            this.index = index;
        }

        public String getKorean() { return korean; }
        public String getHanja() { return hanja; }
        public Element getElement() { return element; }
        public YinYang getYinYang() { return yinYang; }
        public int getIndex() { return index; }

        public static HeavenlyStem fromIndex(int index) {
            return values()[Math.floorMod(index, 10)];
        }
    }

    // ==================== 지지 (Earthly Branches) ====================
    public enum EarthlyBranch {
        JA("자", "子", Element.WATER, YinYang.YANG, 0, "쥐", 23, 1),
        CHUK("축", "丑", Element.EARTH, YinYang.YIN, 1, "소", 1, 3),
        IN("인", "寅", Element.WOOD, YinYang.YANG, 2, "호랑이", 3, 5),
        MYO("묘", "卯", Element.WOOD, YinYang.YIN, 3, "토끼", 5, 7),
        JIN("진", "辰", Element.EARTH, YinYang.YANG, 4, "용", 7, 9),
        SA("사", "巳", Element.FIRE, YinYang.YIN, 5, "뱀", 9, 11),
        O("오", "午", Element.FIRE, YinYang.YANG, 6, "말", 11, 13),
        MI("미", "未", Element.EARTH, YinYang.YIN, 7, "양", 13, 15),
        SHIN("신", "申", Element.METAL, YinYang.YANG, 8, "원숭이", 15, 17),
        YU("유", "酉", Element.METAL, YinYang.YIN, 9, "닭", 17, 19),
        SUL("술", "戌", Element.EARTH, YinYang.YANG, 10, "개", 19, 21),
        HAE("해", "亥", Element.WATER, YinYang.YIN, 11, "돼지", 21, 23);

        private final String korean;
        private final String hanja;
        private final Element element;
        private final YinYang yinYang;
        private final int index;
        private final String animal;
        private final int hourStart;
        private final int hourEnd;

        EarthlyBranch(String korean, String hanja, Element element, YinYang yinYang,
                      int index, String animal, int hourStart, int hourEnd) {
            this.korean = korean;
            this.hanja = hanja;
            this.element = element;
            this.yinYang = yinYang;
            this.index = index;
            this.animal = animal;
            this.hourStart = hourStart;
            this.hourEnd = hourEnd;
        }

        public String getKorean() { return korean; }
        public String getHanja() { return hanja; }
        public Element getElement() { return element; }
        public YinYang getYinYang() { return yinYang; }
        public int getIndex() { return index; }
        public String getAnimal() { return animal; }
        public int getHourStart() { return hourStart; }
        public int getHourEnd() { return hourEnd; }

        public static EarthlyBranch fromIndex(int index) {
            return values()[Math.floorMod(index, 12)];
        }

        /**
         * Get the Earthly Branch for a given hour (0-23).
         * 자시(子時) is 23:00-01:00, 축시(丑時) is 01:00-03:00, etc.
         */
        public static EarthlyBranch fromHour(int hour) {
            int adjusted = (hour + 1) % 24;
            return fromIndex(adjusted / 2);
        }
    }

    // ==================== 지장간 (Hidden Stems) ====================
    /**
     * Hidden Stems within each Earthly Branch.
     * Each branch contains 1-3 hidden stems representing deeper elemental influences.
     */
    public static final Map<EarthlyBranch, List<HeavenlyStem>> HIDDEN_STEMS = Map.ofEntries(
            Map.entry(EarthlyBranch.JA,   List.of(HeavenlyStem.GYE)),
            Map.entry(EarthlyBranch.CHUK, List.of(HeavenlyStem.GI, HeavenlyStem.GYE, HeavenlyStem.SIN)),
            Map.entry(EarthlyBranch.IN,   List.of(HeavenlyStem.GAP, HeavenlyStem.BYEONG, HeavenlyStem.MU)),
            Map.entry(EarthlyBranch.MYO,  List.of(HeavenlyStem.EUL)),
            Map.entry(EarthlyBranch.JIN,  List.of(HeavenlyStem.MU, HeavenlyStem.EUL, HeavenlyStem.GYE)),
            Map.entry(EarthlyBranch.SA,   List.of(HeavenlyStem.BYEONG, HeavenlyStem.MU, HeavenlyStem.GYEONG)),
            Map.entry(EarthlyBranch.O,    List.of(HeavenlyStem.JEONG, HeavenlyStem.GI)),
            Map.entry(EarthlyBranch.MI,   List.of(HeavenlyStem.GI, HeavenlyStem.JEONG, HeavenlyStem.EUL)),
            Map.entry(EarthlyBranch.SHIN, List.of(HeavenlyStem.GYEONG, HeavenlyStem.IM, HeavenlyStem.MU)),
            Map.entry(EarthlyBranch.YU,   List.of(HeavenlyStem.SIN)),
            Map.entry(EarthlyBranch.SUL,  List.of(HeavenlyStem.MU, HeavenlyStem.SIN, HeavenlyStem.JEONG)),
            Map.entry(EarthlyBranch.HAE,  List.of(HeavenlyStem.IM, HeavenlyStem.GAP))
    );

    // ==================== 십신 (Ten Gods) ====================
    public enum TenGod {
        BIJEON("비견", "比肩", "자아, 독립, 경쟁"),
        GEUPJAE("겁재", "劫財", "추진력, 도전, 승부욕"),
        SIKSHIN("식신", "食神", "재능, 표현, 낙천"),
        SANGGWAN("상관", "傷官", "창의, 반항, 예술"),
        PYEONJAE("편재", "偏財", "사교, 투자, 활동"),
        JEONGJAE("정재", "正財", "안정, 근면, 저축"),
        PYEONGWAN("편관", "偏官", "권력, 행동, 결단"),
        JEONGGWAN("정관", "正官", "명예, 질서, 책임"),
        PYEONIN("편인", "偏印", "학문, 직관, 고독"),
        JEONGIN("정인", "正印", "지혜, 자비, 학습");

        private final String korean;
        private final String hanja;
        private final String keywords;

        TenGod(String korean, String hanja, String keywords) {
            this.korean = korean;
            this.hanja = hanja;
            this.keywords = keywords;
        }

        public String getKorean() { return korean; }
        public String getHanja() { return hanja; }
        public String getKeywords() { return keywords; }
    }

    /**
     * Ten Gods relationship mapping.
     * Given the Day Master's element index and the target element index,
     * returns the corresponding Ten God.
     * 
     * The relationship is based on the Five Elements cycle:
     * Same = 비견/겁재, I generate = 식신/상관, I overcome = 편재/정재,
     * Overcomes me = 편관/정관, Generates me = 편인/정인
     */
    private static final TenGod[][] TEN_GOD_TABLE = {
        // Target: WOOD     FIRE        EARTH       METAL       WATER
        // Day Master WOOD
        {TenGod.BIJEON, TenGod.SIKSHIN, TenGod.PYEONJAE, TenGod.PYEONGWAN, TenGod.PYEONIN},
        {TenGod.GEUPJAE, TenGod.SANGGWAN, TenGod.JEONGJAE, TenGod.JEONGGWAN, TenGod.JEONGIN},
        // Day Master FIRE
        {TenGod.PYEONIN, TenGod.BIJEON, TenGod.SIKSHIN, TenGod.PYEONJAE, TenGod.PYEONGWAN},
        {TenGod.JEONGIN, TenGod.GEUPJAE, TenGod.SANGGWAN, TenGod.JEONGJAE, TenGod.JEONGGWAN},
        // Day Master EARTH
        {TenGod.PYEONGWAN, TenGod.PYEONIN, TenGod.BIJEON, TenGod.SIKSHIN, TenGod.PYEONJAE},
        {TenGod.JEONGGWAN, TenGod.JEONGIN, TenGod.GEUPJAE, TenGod.SANGGWAN, TenGod.JEONGJAE},
        // Day Master METAL
        {TenGod.PYEONJAE, TenGod.PYEONGWAN, TenGod.PYEONIN, TenGod.BIJEON, TenGod.SIKSHIN},
        {TenGod.JEONGJAE, TenGod.JEONGGWAN, TenGod.JEONGIN, TenGod.GEUPJAE, TenGod.SANGGWAN},
        // Day Master WATER
        {TenGod.SIKSHIN, TenGod.PYEONJAE, TenGod.PYEONGWAN, TenGod.PYEONIN, TenGod.BIJEON},
        {TenGod.SANGGWAN, TenGod.JEONGJAE, TenGod.JEONGGWAN, TenGod.JEONGIN, TenGod.GEUPJAE}
    };

    /**
     * Get the Ten God relationship between the Day Master and a target stem.
     */
    public static TenGod getTenGod(HeavenlyStem dayMaster, HeavenlyStem target) {
        int masterRow = dayMaster.getIndex();
        int targetCol = target.getElement().ordinal();
        // Adjust row based on same/different yin-yang
        int row = dayMaster.getElement().ordinal() * 2;
        if (dayMaster.getYinYang() != target.getYinYang()) {
            row += 1;
        }
        return TEN_GOD_TABLE[row][targetCol];
    }

    // ==================== 절기 (Solar Terms) ====================
    /**
     * Solar term month boundaries.
     * Index 0 = 입춘(立春, month 1 start), Index 1 = 경칩(驚蟄, month 2 start), ...
     * Each value represents the approximate day-of-year for that solar term.
     * Month boundaries determine the month pillar.
     */
    public static final int[] SOLAR_TERM_MONTH_START_DAYS = {
        35,   // 입춘(立春) ~Feb 4 → month 1 (寅月)
        65,   // 경칩(驚蟄) ~Mar 6 → month 2 (卯月)
        95,   // 청명(清明) ~Apr 5 → month 3 (辰月)
        126,  // 입하(立夏) ~May 6 → month 4 (巳月)
        156,  // 망종(芒種) ~Jun 6 → month 5 (午月)
        187,  // 소서(小暑) ~Jul 7 → month 6 (未月)
        218,  // 입추(立秋) ~Aug 7 → month 7 (申月)
        249,  // 백로(白露) ~Sep 8 → month 8 (酉月)
        279,  // 한로(寒露) ~Oct 8 → month 9 (戌月)
        309,  // 입동(立冬) ~Nov 7 → month 10 (亥月)
        339,  // 대설(大雪) ~Dec 7 → month 11 (子月)
        5     // 소한(小寒) ~Jan 6 → month 12 (丑月) (next year boundary)
    };

    /**
     * 월간(月干) 결정을 위한 연간(年干) 기준표.
     * 연간의 index % 5 로 해당 연도의 월간 시작 오프셋을 구함.
     * 갑/기년 → 병인월(丙寅) 시작, 을/경년 → 무인월(戊寅) 시작, ...
     */
    public static final int[] MONTH_STEM_OFFSET = {2, 4, 6, 8, 0};

    // ==================== 오행 상생상극 (Element Interactions) ====================
    /**
     * 상생 (Generating cycle): 木→火→土→金→水→木
     */
    public static Element getGenerating(Element element) {
        return switch (element) {
            case WOOD -> Element.FIRE;
            case FIRE -> Element.EARTH;
            case EARTH -> Element.METAL;
            case METAL -> Element.WATER;
            case WATER -> Element.WOOD;
        };
    }

    /**
     * 상극 (Overcoming cycle): 木→土→水→火→金→木
     */
    public static Element getOvercoming(Element element) {
        return switch (element) {
            case WOOD -> Element.EARTH;
            case EARTH -> Element.WATER;
            case WATER -> Element.FIRE;
            case FIRE -> Element.METAL;
            case METAL -> Element.WOOD;
        };
    }
}
