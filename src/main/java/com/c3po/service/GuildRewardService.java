package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.setting.SettingCategory;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.guildreward.GuildRewardSettings;
import org.springframework.stereotype.Service;

@Service
public class GuildRewardService extends BaseSettingService<GuildRewardSettings> {
    public GuildRewardService(SettingService settingService, SettingRepository settingRepository) {
        super(settingService, settingRepository);
    }

    @Override
    protected SettingCategory getCategory() {
        return SettingCategory.GUILDREWARDS;
    }

    @Override
    protected GuildRewardSettings getBaseSettings(ScopeTarget target) {
        return new GuildRewardSettings(target);
    }

    @Override
    protected SettingGroupCacheKey<GuildRewardSettings> getCacheKey(ScopeTarget target) {
        return new GuildRewardSettingsKey(target);
    }
}
