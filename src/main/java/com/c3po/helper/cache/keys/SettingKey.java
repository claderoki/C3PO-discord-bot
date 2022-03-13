package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.core.setting.Setting;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class SettingKey extends CacheKey<Setting> {
    private int id;

    @Override
    public String getKeyAffix() {
        return String.valueOf(id);
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }
}
