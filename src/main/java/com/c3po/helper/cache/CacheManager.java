package com.c3po.helper.cache;

import java.util.HashMap;

public class CacheManager {
    private static final HashMap<String, Cache> instances = new HashMap<>();

    public static Cache get(String identifier) {
        return instances.computeIfAbsent(identifier, c -> new Cache());
    }

    public static Cache get() {
        return get("global");
    }

    public static void set(String identifier, Cache cache) {
        instances.put(identifier, cache);
    }

    public static void set(Cache cache) {
        set("global", cache);
    }

}
