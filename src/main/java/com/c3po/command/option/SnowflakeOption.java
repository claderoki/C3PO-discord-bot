package com.c3po.command.option;

import discord4j.common.util.Snowflake;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class SnowflakeOption extends CommandOption<Snowflake> {
    public SnowflakeOption(String key, ApplicationCommandInteractionOptionValue from) {
        super(key, from);
    }

    @Override
    protected Snowflake parseValue() {
        return this.raw.asSnowflake();
    }
}
