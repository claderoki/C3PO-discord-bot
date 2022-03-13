package com.c3po;

import com.c3po.helper.ValueParser;

import java.util.HashMap;
import java.util.Map;

public class MainArguments implements ValueParser {
    private final Map<String, String> values;

    public MainArguments(Map<String, String> values) {
        this.values = values;
    }

    public static MainArguments from(String[] args) {
        Map<String, String> values = new HashMap<>();

        for(String arg: args) {
            String[] split = arg.split("=");
            String key = split[0];
            String value = split[1];
            values.put(key.replace("--", ""), value);
        }

        return new MainArguments(values);
    }

    @Override
    public String optString(String key) {
        return values.get(key);
    }
}
