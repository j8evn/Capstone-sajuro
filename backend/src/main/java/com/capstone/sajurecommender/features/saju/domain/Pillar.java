package com.capstone.sajurecommender.features.saju.domain;

import com.capstone.sajurecommender.features.saju.domain.SajuConstants.*;

/**
 * Represents a single pillar (柱) in the Four Pillars of Destiny.
 * Each pillar consists of a Heavenly Stem (天干) and an Earthly Branch (地支).
 */
public record Pillar(
        HeavenlyStem stem,
        EarthlyBranch branch
) {
    public Element stemElement() {
        return stem.getElement();
    }

    public Element branchElement() {
        return branch.getElement();
    }

    public YinYang yinYang() {
        return stem.getYinYang();
    }

    /**
     * Returns the 60 Sexagenary cycle index (0-59) for this pillar.
     */
    public int sexagenaryCycleIndex() {
        return (stem.getIndex() * 6 + branch.getIndex()) % 60;
    }

    /**
     * Returns the Korean representation like "갑자(甲子)"
     */
    public String toKoreanString() {
        return stem.getKorean() + branch.getKorean() 
               + "(" + stem.getHanja() + branch.getHanja() + ")";
    }
}
