package com.c3po.helper;

public class ValueFormatter implements ValueParser {
    @Override
    public String optString(String key) {
        return key;
    }
}
