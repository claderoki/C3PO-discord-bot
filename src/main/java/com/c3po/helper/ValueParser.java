package com.c3po.helper;

import java.util.function.Function;

public interface ValueParser {
    String optString(String key);

    default String getString(String key) {
        return get(key, (c) -> c, true);
    }

    private <F> F get(String key, Function<String, F> func, boolean required) throws RuntimeException {
        String value = optString(key);
        if (value == null) {
            if (required) {
                throw new RuntimeException(key + " was null but is required.");
            }
            return null;
        }
        return func.apply(value);
    }

    default int getInt(String key) {
        return get(key, Integer::parseInt, true);
    }

    default boolean getBool(String key) {
        return get(key, (value) -> value.equals("1"), true);
    }

    default Integer optInt(String key) {
        return get(key, Integer::parseInt, false);
    }

    default Long optLong(String key) {
        return get(key, Long::parseLong, false);
    }
}
