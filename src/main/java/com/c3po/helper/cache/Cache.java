package com.c3po.helper.cache;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.function.Function;

public class Cache {
    public final HashMap<String, CacheItem<Object>> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(CacheKey<T> key) {
        String fullKey = key.getFullKey();
        CacheItem<Object> cacheItem = cache.get(fullKey);

        if (cacheItem == null) {
            return null;
        }

        if (cacheItem.isExpired()) {
            remove(key);
            return null;
        }

        return (T)cacheItem.getValue();
    }

    public <T> void set(CacheKey<T> key, T value) {
        String fullKey = key.getFullKey();

        Duration timeToLive = key.getTimeToLive();
        OffsetDateTime expirationDate = timeToLive == null ? null : OffsetDateTime.now(ZoneOffset.UTC).plus(key.getTimeToLive());
        cache.put(fullKey, new CacheItem<>(value, expirationDate));
    }

    public <T> void remove(CacheKey<T> key) {
        String fullKey = key.getFullKey();
        cache.remove(fullKey);
    }

    public <T> T computeIfAbsent(CacheKey<T> key, Function<CacheKey<T>, T> or) {
        T value = get(key);
        if (value != null) {
            return value;
        }

        value = or.apply(key);
        if (value != null) {
            set(key, value);
            return value;
        }

        return null;
    }

    public void removeExpiredItems() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
