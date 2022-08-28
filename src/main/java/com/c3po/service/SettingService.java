package com.c3po.service;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.keys.SettingCodeKey;
import com.c3po.helper.cache.keys.SettingIdKey;
import com.c3po.helper.cache.keys.SettingKey;
import com.c3po.core.setting.Setting;
import com.c3po.helper.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    private void cacheIdAndCodes() {
        HashMap<String, HashMap<String, Integer>> identifiers = settingRepository.getSettingIdentifiers();
        for (Map.Entry<String, HashMap<String, Integer>> entry: identifiers.entrySet()) {
            for (Map.Entry<String, Integer> mapping: entry.getValue().entrySet()) {
                String category = entry.getKey();
                String code = mapping.getKey();
                Integer id = mapping.getValue();

                SettingIdKey idKey = new SettingIdKey(category, code);
                SettingCodeKey codeKey = new SettingCodeKey(id);

                CacheManager.get().set(idKey, id);
                CacheManager.get().set(codeKey, code);
            }
        }
    }

    protected Integer getCachedId(String category, String code) {
        SettingIdKey key = new SettingIdKey(category, code);
        return CacheManager.get().get(key);
    }

    public Integer getId(String category, String code) {
        Integer id = getCachedId(category, code);
        if (id != null) {
            return id;
        }
        cacheIdAndCodes();
        return getCachedId(category, code);
    }

    protected String getCachedCode(Integer id) {
        SettingCodeKey key = new SettingCodeKey(id);
        return CacheManager.get().get(key);
    }

    public String getCode(Integer id) {
        String code = getCachedCode(id);
        if (code != null) {
            return code;
        }
        cacheIdAndCodes();
        return getCode(id);
    }

    public Setting getSetting(Integer id) {
        return CacheManager.get().computeIfAbsent(new SettingKey(id), key -> {
            Setting setting = settingRepository.getSetting(id);
            if (setting != null) {
                CacheManager.get().set(key, setting);
            }
            return setting;
        });
    }

}
