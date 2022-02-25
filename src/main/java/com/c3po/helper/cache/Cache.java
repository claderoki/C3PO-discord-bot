package com.c3po.helper.cache;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class Cache {
    public static HashMap<String, Object> cache = new HashMap<>();
    private static final HashMap<String, OffsetDateTime> expiryDates = new HashMap<>();

    public static <T> T get(CacheKey<T> key) {
        String fullKey = key.getFullKey();
        OffsetDateTime expiryDate = expiryDates.get(fullKey);
        if (expiryDate != null && OffsetDateTime.now(ZoneOffset.UTC).isAfter(expiryDate)) {
            expiryDates.remove(fullKey);
            cache.remove(fullKey);
            return null;
        }

        Object value = cache.get(fullKey);
        if (value == null) {
            return null;
        }

        return (T)value;
    }

    public static <T> void set(CacheKey<T> key, T value) {
        String fullKey = key.getFullKey();

        Duration timeToLive = key.getTimeToLive();
        if (timeToLive != null) {
            expiryDates.put(fullKey, OffsetDateTime.now(ZoneOffset.UTC).plus(key.getTimeToLive()));
        }
        cache.put(fullKey, value);
    }

    public static <T> void remove(CacheKey<T> key) {
        String fullKey = key.getFullKey();
        cache.remove(fullKey);
        expiryDates.remove(fullKey);
    }

}
