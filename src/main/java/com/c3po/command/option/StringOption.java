package com.c3po.command.option;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class StringOption extends CommandOption<String> {
    public StringOption(String key, ApplicationCommandInteractionOptionValue from) {
        super(key, from);
    }

    @Override
    protected String parseValue() {
        return this.raw.asString();
    }
}
