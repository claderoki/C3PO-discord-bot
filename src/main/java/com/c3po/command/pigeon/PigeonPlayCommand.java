package com.c3po.command.pigeon;

import com.c3po.model.pigeon.stat.StatType;

public class PigeonPlayCommand extends PigeonStatCommand {
    protected PigeonPlayCommand(PigeonCommandGroup group) {
        super(group, "play", "no message");
    }

    @Override
    protected String getMessage() {
        return "Your pigeon looks bored. You decide to play with it!";
    }

    @Override
    protected StatType getStatType() {
        return StatType.HAPPINESS;
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
