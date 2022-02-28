package com.c3po.model;

import com.c3po.helper.ValueParser;

public abstract class BaseSettings implements ValueParser {
    public String optString(String value) {
        return value;
    }

    public abstract void set(String key, String value);
}
