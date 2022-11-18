package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.setting.SettingCategory;
import com.c3po.helper.cache.keys.ActivityTrackerSettingsKey;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.guildreward.ActivityTrackerSettings;
import org.springframework.stereotype.Service;

@Service
public class ActivityTrackerService extends BaseSettingService<ActivityTrackerSettings> {
    public ActivityTrackerService(SettingService settingService, SettingRepository settingRepository) {
        super(settingService, settingRepository);
    }

    @Override
    protected SettingCategory getCategory() {
        return SettingCategory.ACTIVITY_TRACKER;
    }

    @Override
    protected ActivityTrackerSettings getBaseSettings(ScopeTarget target) {
        return new ActivityTrackerSettings(target);
    }

    @Override
    protected SettingGroupCacheKey<ActivityTrackerSettings> getCacheKey(ScopeTarget target) {
        return new ActivityTrackerSettingsKey(target);
    }
}
