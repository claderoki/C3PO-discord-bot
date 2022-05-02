package com.c3po.model.pigeon.stat;

public class PigeonCleanliness extends PigeonStat {
    public PigeonCleanliness(long value) {
        super(value, StatType.CLEANLINESS);
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
