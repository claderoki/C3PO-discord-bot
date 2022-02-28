package com.c3po.helper.cache.keys;

import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;
import com.c3po.model.MilkywaySettings;

import java.time.Duration;

public class MilkywaySettingsKey extends SettingGroupCacheKey<MilkywaySettings> {

    public MilkywaySettingsKey(SettingScopeTarget target) {
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
