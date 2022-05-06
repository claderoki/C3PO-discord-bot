package com.c3po.model.pigeon.stat;

import com.c3po.model.pigeon.stat.core.Stat;

public class PigeonCleanliness extends Stat {
    public PigeonCleanliness(long value) {
        super(value, StatType.CLEANLINESS, StatCategory.PIGEON);
    }

    @Override
    public String getEmoji() {
        return "\uD83D\uDCA9";
    }

    public Integer getMax() {
        return 100;
    }

    public Integer getMin() {
        return 0;
    }

}
