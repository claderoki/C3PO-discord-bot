package com.c3po.model.pigeon.stat;

import com.c3po.model.pigeon.stat.core.Stat;

public class StatFactory {
    public static Stat create(StatType type, long value) {
        return switch (type) {
            case GOLD -> new HumanGold(value);
            case HEALTH -> new PigeonHealth(value);
            case HAPPINESS -> new PigeonHappiness(value);
            case CLEANLINESS -> new PigeonCleanliness(value);
            case EXPERIENCE -> new PigeonExperience(value);
            case FOOD -> new PigeonFood(value);
        };
    }
}
