package com.capstone.sajurecommender.features.saju.domain;

import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Four Pillars (四柱) of Destiny: Year, Month, Day, Hour.
 */
public record FourPillars(
        Pillar yearPillar,
        Pillar monthPillar,
        Pillar dayPillar,
        Pillar hourPillar
) {
    /**
     * The Day Master (日干) — the Heavenly Stem of the Day Pillar.
     * This is the most important element in Saju analysis as it represents the self.
     */
    public HeavenlyStem dayMaster() {
        return dayPillar.stem();
    }

    /**
     * Collect all 8 characters (stems and branches from all 4 pillars).
     */
    public List<Object> allCharacters() {
        return List.of(
                yearPillar.stem(), yearPillar.branch(),
                monthPillar.stem(), monthPillar.branch(),
                dayPillar.stem(), dayPillar.branch(),
                hourPillar.stem(), hourPillar.branch()
        );
    }

    /**
     * Count the distribution of Five Elements across all 8 characters.
     */
    public Map<Element, Integer> elementDistribution() {
        Map<Element, Integer> dist = new EnumMap<>(Element.class);
        for (Element e : Element.values()) {
            dist.put(e, 0);
        }

        // Count stems
        for (Pillar p : List.of(yearPillar, monthPillar, dayPillar, hourPillar)) {
            dist.merge(p.stem().getElement(), 1, Integer::sum);
            dist.merge(p.branch().getElement(), 1, Integer::sum);
        }

        return dist;
    }

    /**
     * Count Yin vs Yang distribution.
     */
    public Map<YinYang, Integer> yinYangBalance() {
        Map<YinYang, Integer> balance = new EnumMap<>(YinYang.class);
        balance.put(YinYang.YANG, 0);
        balance.put(YinYang.YIN, 0);

        for (Pillar p : List.of(yearPillar, monthPillar, dayPillar, hourPillar)) {
            balance.merge(p.stem().getYinYang(), 1, Integer::sum);
            balance.merge(p.branch().getYinYang(), 1, Integer::sum);
        }

        return balance;
    }
}
