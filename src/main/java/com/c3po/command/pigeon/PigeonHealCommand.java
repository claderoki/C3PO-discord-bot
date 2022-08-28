package com.c3po.command.pigeon;

import com.c3po.model.pigeon.stat.StatType;
import org.springframework.stereotype.Component;

@Component
public class PigeonHealCommand extends PigeonStatCommand {
    protected PigeonHealCommand() {
        super("heal", "no message");
    }

    @Override
    protected String getMessage() {
        return "You give your pigeon some health. It's health is refilled!";
    }

    @Override
    protected StatType getStatType() {
        return StatType.HEALTH;
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
