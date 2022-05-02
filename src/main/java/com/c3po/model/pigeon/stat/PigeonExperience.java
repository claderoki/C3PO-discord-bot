package com.c3po.model.pigeon.stat;

public class PigeonExperience extends PigeonStat {
    public PigeonExperience(long value) {
        super(value, StatType.EXPERIENCE);
    }

    @Override
    public String getEmoji() {
        return "\uD83D\uDCCA";
    }
}
