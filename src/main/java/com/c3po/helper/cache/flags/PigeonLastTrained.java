package com.c3po.helper.cache.flags;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PigeonLastTrained extends Flag {
    private final int pigeonId;

    @Override
    public String getKeyAffix() {
        return String.valueOf(pigeonId);
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofMinutes(10);
    }
}
