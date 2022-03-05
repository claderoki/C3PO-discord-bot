package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.helper.setting.SettingScopeTarget;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class HumanIdKey extends CacheKey<Integer> {
    private SettingScopeTarget target;

    @Override
    public String getKeyAffix() {
        return target.toString();
    }

    @Override
    public Duration getTimeToLive() {
        return null;
    }
}
