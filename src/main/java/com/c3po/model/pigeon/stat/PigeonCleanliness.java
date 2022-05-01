package com.c3po.model.pigeon.stat;

public class PigeonCleanliness extends PigeonStat {
    public PigeonCleanliness(long value) {
        super(value);
    }

    @Override
    public String getEmoji() {
        return "\uD83D\uDCA9";
    }
}
