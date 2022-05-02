package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.model.exploration.FullExplorationLocation;

import java.time.Duration;
import java.util.Map;

public class ExplorationLocationsKey extends CacheKey<Map<Integer, FullExplorationLocation>> {
    @Override
    public String getKeyAffix() {
        return "";
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(10);
    }

}
