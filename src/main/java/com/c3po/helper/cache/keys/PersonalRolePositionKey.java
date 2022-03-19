package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.helper.cache.CacheKey;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PersonalRolePositionKey extends CacheKey<Integer> {
    private final ScopeTarget target;

    @Override
    public String getKeyAffix() {
        return target.toString();
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }

}
