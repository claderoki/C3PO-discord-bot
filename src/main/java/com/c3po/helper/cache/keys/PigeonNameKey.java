package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PigeonNameKey extends CacheKey<String> {
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