package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class AttributeIdKey extends CacheKey<Integer> {
    private String code;

    public String getKeyAffix() {
        return code;
    }

    @Override
    public Duration getTimeToLive() {
        return null;
    }
}
