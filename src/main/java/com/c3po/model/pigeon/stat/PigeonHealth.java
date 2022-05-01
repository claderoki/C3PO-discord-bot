package com.c3po.model.pigeon.stat;

public class PigeonHealth extends PigeonStat {
    public PigeonHealth(long value) {
        super(value);
    }

    @Override
    public String getEmoji() {
        return "❤️";
    }
}
