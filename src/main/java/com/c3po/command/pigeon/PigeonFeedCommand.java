package com.c3po.command.pigeon;

import com.c3po.model.pigeon.stat.StatType;
import org.springframework.stereotype.Component;

@Component
public class PigeonFeedCommand extends PigeonStatCommand {
    protected PigeonFeedCommand() {
        super("feed", "no message");
    }

    @Override
    protected String getMessage() {
        return "You give your pigeon some seeds. It's energy is refilled!";
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
