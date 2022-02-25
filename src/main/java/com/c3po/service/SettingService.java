package com.c3po.service;

import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.BaseSettings;

public abstract class SettingService extends Service {

//    public static <T extends BaseSettings> T getSettings(SettingGroupCacheKey<T> key, String category) {
//        T settings = Cache.get(key);
//        if (settings != null) {
//            return settings;
//        }
//
//        settings = new T(key.getTarget());
//        for(SettingValue value: SettingRepository.db().getHydratedSettingValues(key.getTarget(), category).values()) {
//            String settingKey = SettingCache.getCode(value.getSettingId());
//            settings.set(settingKey, value.getValue());
//        }
//        Cache.set(key, settings);
//        return settings;
//    }
}
