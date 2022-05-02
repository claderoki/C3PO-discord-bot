package com.c3po.model.pigeon.stat;

public class PigeonHappiness extends PigeonStat {
    public PigeonHappiness(long value) {
        super(value, StatType.HAPPINESS);
    }

    @Override
    public String getEmoji() {
        return "🌻";
    }

    public Integer getMax() {
        return 100;
    }

    public Integer getMin() {
        return 0;
    }

}
