package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.helper.setting.KnownCategory;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import com.c3po.helper.setting.cache.SettingCache;
import com.c3po.model.GuildRewardsSettings;

public class GuildRewardService extends SettingService {
    public static GuildRewardsSettings getSettings(SettingScopeTarget target) {
        GuildRewardSettingsKey key = new GuildRewardSettingsKey(target);
        GuildRewardsSettings settings = Cache.get(key);
        if (settings != null) {
            return settings;
        }

        settings = new GuildRewardsSettings(target);
        for(SettingValue value: SettingRepository.db().getHydratedSettingValues(target, KnownCategory.GUILDREWARDS).values()) {
            String settingKey = SettingCache.getCode(value.getSettingId());
            settings.set(settingKey, value.getValue());
        }
        Cache.set(key, settings);
        return settings;
    }

}
