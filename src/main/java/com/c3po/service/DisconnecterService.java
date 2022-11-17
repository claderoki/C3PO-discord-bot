package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.setting.KnownCategory;
import com.c3po.helper.cache.keys.DisconnecterSettingsKey;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.disconnecter.DisconnecterSettings;
import org.springframework.stereotype.Service;

@Service
public class DisconnecterService extends BaseSettingService<DisconnecterSettings> {
    public DisconnecterService(SettingService settingService, SettingRepository settingRepository) {
        super(settingService, settingRepository);
    }

    @Override
    protected String getCategory() {
        return KnownCategory.DISCONNECTER;
    }

    protected DisconnecterSettings getBaseSettings(ScopeTarget target) {
        return new DisconnecterSettings(target);
    }

    @Override
    protected SettingGroupCacheKey<DisconnecterSettings> getCacheKey(ScopeTarget target) {
        return new DisconnecterSettingsKey(target);
    }
}
