package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.CacheKey;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;

public class GuildRewardsSettingsKey extends CacheKey<GuildRewardsSettings> {
    private final SettingScopeTarget target;

    public GuildRewardsSettingsKey(SettingScopeTarget target) {
        this.target = target;
    }

    @Override
    public String getFullKey() {
        return "guildrewards:" + target;
    }

    @Override
    public int getTimeToLive() {
        return 0;
    }

    public void abc() {
        GuildRewardsSettings a = Cache.get(new GuildRewardsSettingsKey(SettingScopeTarget.guild(123L)));
    }
}
