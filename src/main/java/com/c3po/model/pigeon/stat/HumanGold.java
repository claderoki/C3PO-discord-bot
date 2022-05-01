package com.c3po.model.pigeon.stat;

import com.c3po.helper.Emoji;

public class HumanGold extends Stat {
    public HumanGold(long value) {
        super(value);
    }

    @Override
    public String getEmoji() {
        return Emoji.EURO;
    }
}
