package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class TaskRunsKey extends CacheKey<Map<String, LocalDateTime>> {
    @Override
    public String getKeyAffix() {
        return "";
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }
}
