package com.c3po.helper.cache;

import java.time.Duration;

public abstract class CacheKey<T> {
    public String getFullKey() {
        return this.getClass().getSimpleName() +":"+ getKeyAffix();
    }
    public abstract String getKeyAffix();
    public abstract Duration getTimeToLive();
}
