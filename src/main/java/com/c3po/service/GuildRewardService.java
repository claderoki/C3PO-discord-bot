package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.core.setting.KnownCategory;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.guildreward.GuildRewardSettings;

public class GuildRewardService extends Service {
    private final SettingRepository settingRepository = SettingRepository.db();
    private final SettingService settingService = new SettingService();

    public GuildRewardSettings getSettings(ScopeTarget target) {
        return CacheManager.get().computeIfAbsent(new GuildRewardSettingsKey(target), key -> {
            GuildRewardSettings settings = new GuildRewardSettings(target);
            for(PropertyValue value: settingRepository.getHydratedPropertyValues(target, KnownCategory.GUILDREWARDS).values()) {
                String settingKey = settingService.getCode(value.getParentId());
                settings.set(settingKey, value.getValue());
            }
            return settings;
        });
    }
}
