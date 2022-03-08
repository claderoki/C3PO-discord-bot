package com.c3po.command.option;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class BoolOption extends CommandOption<Boolean> {
    public BoolOption(String key, ApplicationCommandInteractionOptionValue from) {
        super(key, from);
    }

    @Override
    protected Boolean parseValue() {
        return this.raw.asBoolean();
    }
}
