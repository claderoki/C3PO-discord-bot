package com.c3po.helper.cache.keys;

import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardSettings;

import java.time.Duration;

public class GuildRewardSettingsKey extends SettingGroupCacheKey<GuildRewardSettings> {

    public GuildRewardSettingsKey(SettingScopeTarget target) {
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
