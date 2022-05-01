package com.c3po.model.pigeon.stat;

public class PigeonFood extends PigeonStat {
    public PigeonFood(long value) {
        super(value);
    }

    @Override
    public String getEmoji() {
        return "\uD83C\uDF3E";
    }
}
