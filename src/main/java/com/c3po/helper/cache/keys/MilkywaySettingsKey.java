package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.model.milkyway.MilkywaySettings;

import java.time.Duration;

public class MilkywaySettingsKey extends SettingGroupCacheKey<MilkywaySettings> {

    public MilkywaySettingsKey(ScopeTarget target) {
        super(target);
    }

    @Override
    public String getKeyAffix() {
        return target.toString();
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }

}
