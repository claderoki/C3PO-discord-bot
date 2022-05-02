package com.c3po.model.pigeon.stat;

public class PigeonFood extends PigeonStat {
    public PigeonFood(long value) {
        super(value, StatType.FOOD);
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
