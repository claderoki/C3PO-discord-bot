package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PigeonIdKey extends CacheKey<Integer> {
    private final int humanId;

    @Override
    public String getKeyAffix() {
        return String.valueOf(humanId);
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }
}