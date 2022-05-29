package com.c3po.command.pigeon;

import com.c3po.model.pigeon.stat.StatType;

public class PigeonCleanCommand extends PigeonStatCommand {
    protected PigeonCleanCommand(PigeonCommandGroup group) {
        super(group, "clean", "no message");
    }

    @Override
    protected String getMessage() {
        return "Your pigeon leaves dirty food prints on the floor! You decide to give it a bath.";
    }

    @Override
    protected StatType getStatType() {
        return StatType.CLEANLINESS;
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
