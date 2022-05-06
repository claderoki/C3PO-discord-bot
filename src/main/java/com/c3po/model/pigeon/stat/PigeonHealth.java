package com.c3po.model.pigeon.stat;

import com.c3po.model.pigeon.stat.core.Stat;

public class PigeonHealth extends Stat {
    public PigeonHealth(long value) {
        super(value, StatType.HEALTH, StatCategory.PIGEON);
    }

    @Override
    public String getEmoji() {
        return "❤️";
    }

    public Integer getMax() {
        return 100;
    }

    public Integer getMin() {
        return 0;
    }

}
