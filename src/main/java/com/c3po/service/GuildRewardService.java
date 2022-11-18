package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.core.setting.KnownCategory;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.guildreward.GuildRewardSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildRewardService {
    private final SettingRepository settingRepository;
    private final SettingService settingService;

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
