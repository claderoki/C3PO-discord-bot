package com.c3po.helper.cache.keys;

import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;

import java.time.Duration;

public class GuildRewardSettingsKey extends SettingGroupCacheKey<GuildRewardsSettings> {

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
