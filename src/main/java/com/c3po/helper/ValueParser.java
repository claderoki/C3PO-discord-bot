package com.c3po.helper;

import java.util.function.Function;

public interface ValueParser {
    String optString(String key);

    private <F> F get(String key, Function<String, F> func) throws RuntimeException {
        String value = optString(key);
        if (value == null) {
            throw new RuntimeException(key + " was null but is required.");
        }
        return func.apply(value);
    }

    private <F> F getOr(String key, Function<String, F> func, F defaultValue) throws RuntimeException {
        String value = optString(key);
        if (value == null) {
            return defaultValue;
        }
        return func.apply(value);
    }

    private <F> F opt(String key, Function<String, F> func) throws RuntimeException {
        return getOr(key, func, null);
    }

    default String getString(String key) {
        return get(key, (c) -> c);
    }

    default int getInt(String key) {
        return get(key, Integer::parseInt);
    }

    default boolean getBool(String key) {
        return get(key, (value) -> value.equals("1"));
    }

    default Integer optInt(String key) {
        return opt(key, Integer::parseInt);
    }

    default Long optLong(String key) {
        return opt(key, Long::parseLong);
    }
}
