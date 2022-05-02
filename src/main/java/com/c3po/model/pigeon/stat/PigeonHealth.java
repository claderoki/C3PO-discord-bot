package com.c3po.model.pigeon.stat;

public class PigeonHealth extends PigeonStat {
    public PigeonHealth(long value) {
        super(value, StatType.HEALTH);
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
