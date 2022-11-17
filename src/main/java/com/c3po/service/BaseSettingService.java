package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.BaseSettings;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseSettingService<T extends BaseSettings> {
    protected final SettingService settingService;
    protected final SettingRepository settingRepository;

    protected abstract String getCategory();
    protected abstract T getBaseSettings(ScopeTarget target);
    protected abstract SettingGroupCacheKey<T> getCacheKey(ScopeTarget target);

    private T fetchSettings(ScopeTarget target) {
        var settings = getBaseSettings(target);
        for(PropertyValue value: settingRepository.getHydratedPropertyValues(target, getCategory()).values()) {
            String settingKey = settingService.getCode(value.getParentId());
            settings.set(settingKey, value.getValue());
        }
        return settings;
    }

    public final T getSettings(ScopeTarget target) {
        return CacheManager.get().computeIfAbsent(getCacheKey(target), k -> fetchSettings(target));
    }
}
