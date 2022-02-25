package com.c3po.helper.cache;

import java.time.Duration;

public abstract class CacheKey<T> {
    public abstract String getFullKey();
    public abstract Duration getTimeToLive();
}
