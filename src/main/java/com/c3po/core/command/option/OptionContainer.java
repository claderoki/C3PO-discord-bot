package com.c3po.core.command.option;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class OptionContainer {
    Map<String, CommandOption<?>> options;

    public CommandOption<?> get(String key) {
        return options.get(key);
    }

    protected <T> T getValue(String key) {
        return (T) get(key).getValue();
    }

    protected <T> T optValue(String key) {
        CommandOption<?> option = get(key);
        if (option == null) {
            return null;
        }
        return (T)option.getValue();
    }

    public String getString(String key) {
        return getValue(key);
    }

    public String optString(String key) {
        return optValue(key);
    }

    public long getLong(String key) {
        return getValue(key);
    }

    public Long optLong(String key) {
        return optValue(key);
    }

    public Boolean optBool(String key) {
        return optValue(key);
    }

    public boolean getBool(String key) {
        return getValue(key);
    }

    public Double getDouble(String key) {
        return getValue(key);
    }

    public Double optDouble(String key) {
        return optValue(key);
    }

    public Snowflake getSnowflake(String key) {
        return getValue(key);
    }

    public Snowflake optSnowflake(String key) {
        return optValue(key);
    }


}
