package com.c3po.model.pigeon.stat;

import com.c3po.helper.Emoji;
import com.c3po.model.pigeon.stat.core.Stat;

public class HumanGold extends Stat {
    public HumanGold(long value) {
        super(value, StatType.GOLD, StatCategory.HUMAN);
    }

    @Override
    public String getEmoji() {
        return Emoji.EURO;
    }

    public Integer getMin() {
        return 0;
    }

}
