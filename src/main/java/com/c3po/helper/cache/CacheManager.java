package com.c3po.helper.cache;

import java.util.HashMap;

public class CacheManager {
    private static HashMap<String, Cache> instances = new HashMap<>();

    public static Cache get(String identifier) {
        return instances.get(identifier);
    }

    public static Cache get() {
        return instances.get("global");
    }

    public static void set(String identifier, Cache cache) {
        instances.put(identifier, cache);
    }

    public static void set(Cache cache) {
        instances.put("global", cache);
    }

}
