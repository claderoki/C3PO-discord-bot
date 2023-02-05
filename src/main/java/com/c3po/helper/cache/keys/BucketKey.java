package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.core.command.BucketData;
import com.c3po.helper.cache.CacheKey;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class BucketKey extends CacheKey<BucketData> {
    private final ScopeTarget scopeTarget;
    private final String commandName;

    @Override
    public String getKeyAffix() {
        return scopeTarget + ":" + commandName;
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(2);
    }
}
