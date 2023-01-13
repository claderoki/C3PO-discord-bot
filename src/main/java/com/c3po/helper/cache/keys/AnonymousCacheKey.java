package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class AnonymousCacheKey<T> extends CacheKey<T> {
    private final String affix;
    private final Duration timeToLive;

    public String getKeyAffix() {
        return affix;
    }

    @Override
    public Duration getTimeToLive() {
        return timeToLive;
    }
}
