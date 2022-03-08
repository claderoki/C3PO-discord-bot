package com.c3po.command.option;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class DoubleOption extends CommandOption<Double> {
    public DoubleOption(String key, ApplicationCommandInteractionOptionValue from) {
        super(key, from);
    }

    @Override
    protected Double parseValue() {
        return this.raw.asDouble();
    }
}
