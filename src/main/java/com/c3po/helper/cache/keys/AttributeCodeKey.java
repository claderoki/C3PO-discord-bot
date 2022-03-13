package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class AttributeCodeKey extends CacheKey<String> {
    private int id;

    public String getKeyAffix() {
        return String.valueOf(id);
    }

    @Override
    public Duration getTimeToLive() {
        return null;
    }
}
