package com.c3po.core.command.option;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class LongOption extends CommandOption<Long> {
    public LongOption(String key, ApplicationCommandInteractionOptionValue from) {
        super(key, from);
    }

    @Override
    protected Long parseValue() {
        return this.raw.asLong();
    }
}
