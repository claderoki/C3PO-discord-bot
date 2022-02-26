package com.c3po.helper;

import java.time.Duration;

public interface ValueParser {
    String getString(String key);
    String optString(String key);

    default int getInt(String key) {
        String value = getString(key);
        return Integer.parseInt(value);
    }

    default boolean getBool(String key) {
        String value = getString(key);
        return value.equals("1");
    }

    default Duration getDuration(String key) {
        String value = getString(key);
        return DurationFormatter.parse(value);
    }

    default Integer optInt(String key) {
        String value = optString(key);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    default Long optLong(String key) {
        String value = optString(key);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }
}
