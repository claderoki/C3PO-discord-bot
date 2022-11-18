package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.model.guildreward.ActivityTrackerSettings;

import java.time.Duration;

public class ActivityTrackerSettingsKey extends SettingGroupCacheKey<ActivityTrackerSettings> {
    public ActivityTrackerSettingsKey(ScopeTarget target) {
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
