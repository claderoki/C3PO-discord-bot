package com.c3po.command.pigeon;

import com.c3po.model.pigeon.stat.StatType;

public class PigeonFeedCommand extends PigeonStatCommand {
    protected PigeonFeedCommand(PigeonCommandGroup group) {
        super(group, "feed", "no message");
    }

    @Override
    protected StatType getStatType() {
        return StatType.FOOD;
    }

    @Override
    protected int getGain() {
        return 15;
    }

    @Override
    protected int getCost() {
        return 20;
    }
}
