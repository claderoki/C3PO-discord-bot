package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.core.setting.KnownCategory;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.model.guildreward.GuildRewardSettings;

public class GuildRewardService extends Service {
    public static GuildRewardSettings getSettings(ScopeTarget target) {
        return Cache.computeIfAbsent(new GuildRewardSettingsKey(target), key -> {
            GuildRewardSettings settings = new GuildRewardSettings(target);
            for(PropertyValue value: SettingRepository.db().getHydratedPropertyValues(target, KnownCategory.GUILDREWARDS).values()) {
                String settingKey = SettingService.getCode(value.getParentId());
                settings.set(settingKey, value.getValue());
            }
            return settings;
        });
    }
}
