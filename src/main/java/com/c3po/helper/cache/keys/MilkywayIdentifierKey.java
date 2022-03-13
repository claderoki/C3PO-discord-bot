package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.core.ScopeTarget;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class MilkywayIdentifierKey extends CacheKey<Long> {
    private ScopeTarget target;

    @Override
    public String getKeyAffix() {
        return target.toString();
    }

    @Override
    public Duration getTimeToLive() {
        return null;
    }
}
