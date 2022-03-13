package com.c3po.core.command.option;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.Getter;

@Getter
public abstract class CommandOption<T> {
    protected final String key;
    protected final T value;
    protected final ApplicationCommandInteractionOptionValue raw;

    protected abstract T parseValue();

    public CommandOption(String key, ApplicationCommandInteractionOptionValue from) {
        this.raw = from;
        this.key = key;
        this.value = parseValue();
    }

}
