package com.c3po.helper.cache;

public abstract class CacheKey<T> {
    public abstract String getFullKey();
    public abstract int getTimeToLive();
}
