package com.c3po.model.pigeon.stat;

public class PigeonHappiness extends PigeonStat {
    public PigeonHappiness(long value) {
        super(value);
    }

    @Override
    public String getEmoji() {
        return "🌻";
    }
}
