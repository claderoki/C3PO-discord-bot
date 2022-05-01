package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PigeonNameKey extends CacheKey<String> {
    private final int id;

    @Override
    public String getKeyAffix() {
        return String.valueOf(id);
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }
}