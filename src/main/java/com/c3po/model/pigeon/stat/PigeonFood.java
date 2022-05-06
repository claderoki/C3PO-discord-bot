package com.c3po.model.pigeon.stat;

import com.c3po.model.pigeon.stat.core.Stat;

public class PigeonFood extends Stat {
    public PigeonFood(long value) {
        super(value, StatType.FOOD, StatCategory.PIGEON);
    }

    @Override
    public String getEmoji() {
        return "\uD83C\uDF3E";
    }

    public Integer getMax() {
        return 100;
    }

    public Integer getMin() {
        return 0;
    }

}
