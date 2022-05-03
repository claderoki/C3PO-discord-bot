package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.SettingCodeKey;
import com.c3po.helper.cache.keys.SettingIdKey;
import com.c3po.helper.cache.keys.SettingKey;
import com.c3po.core.setting.Setting;

import java.util.HashMap;
import java.util.Map;

public class SettingService extends Service {

    private static void cacheIdAndCodes() {
        HashMap<String, HashMap<String, Integer>> identifiers = SettingRepository.db().getSettingIdentifiers();
        for (Map.Entry<String, HashMap<String, Integer>> entry: identifiers.entrySet()) {
            for (Map.Entry<String, Integer> mapping: entry.getValue().entrySet()) {
                String category = entry.getKey();
                String code = mapping.getKey();
                Integer id = mapping.getValue();

                SettingIdKey idKey = new SettingIdKey(category, code);
                SettingCodeKey codeKey = new SettingCodeKey(id);

                Cache.set(idKey, id);
                Cache.set(codeKey, code);
            }
        }
    }

    protected static Integer getCachedId(String category, String code) {
        SettingIdKey key = new SettingIdKey(category, code);
        return Cache.get(key);
    }

    public static Integer getId(String category, String code) {
        Integer id = getCachedId(category, code);
        if (id != null) {
            return id;
        }
        cacheIdAndCodes();
        return getCachedId(category, code);
    }

    protected static String getCachedCode(Integer id) {
        SettingCodeKey key = new SettingCodeKey(id);
        return Cache.get(key);
    }

    public static String getCode(Integer id) {
        String code = getCachedCode(id);
        if (code != null) {
            return code;
        }
        cacheIdAndCodes();
        return getCode(id);
    }

    public static Setting getSetting(Integer id) {
        return Cache.computeIfAbsent(new SettingKey(id), key -> {
            Setting setting = SettingRepository.db().getSetting(id);
            if (setting != null) {
                Cache.set(key, setting);
            }
            return setting;
        });
    }

}
