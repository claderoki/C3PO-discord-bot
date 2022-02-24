package com.c3po.helper.cache;

import java.util.HashMap;

public class Cache {
    public static HashMap<String, Object> cache = new HashMap<>();

    public static <T> T get(CacheKey<T> key) {
        return (T)cache.get(key.getFullKey());
    }

    public static <T> void set(CacheKey<T> key, T value) {
        cache.put(key.getFullKey(), value);
    }

}
