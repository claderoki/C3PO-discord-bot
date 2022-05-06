package com.c3po.model.pigeon.stat;

import com.c3po.model.pigeon.stat.core.Stat;

public class PigeonExperience extends Stat {
    public PigeonExperience(long value) {
        super(value, StatType.EXPERIENCE, StatCategory.PIGEON);
    }

    @Override
    public String getEmoji() {
        return "\uD83D\uDCCA";
    }
}
