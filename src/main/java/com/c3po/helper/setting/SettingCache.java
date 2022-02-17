package com.c3po.helper.setting;

import com.c3po.connection.repository.SettingRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingCache {
    private static HashMap<String, HashMap<String, Setting>> cachedSettings = new HashMap<>();
    private static HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();

    private static HashMap<String, HashMap<Integer, SettingValue>> cachedValues = new HashMap<>();

    private static HashMap<String, Setting> getUncachedSettings(String category) throws SQLException {
        HashMap<String, Setting> settings = SettingRepository.db().getSettings(category);
        lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
        cachedSettings.put(category, settings);
        return settings;
    }

    private static boolean shouldRefresh(String key) {
        OffsetDateTime lastRefresh = lastRefreshes.get(key);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static HashMap<String, Setting> getSettings(String category) throws SQLException {
        if (shouldRefresh(category)) {
            return getUncachedSettings(category);
        }
        return cachedSettings.computeIfAbsent(category, (c) -> new HashMap<>());
    }

    public static HashMap<Integer, SettingValue> getValues(SettingScopeTarget target, String category) throws SQLException {
        StringBuilder keyBuilder = new StringBuilder("values");
        keyBuilder.append(category);
        if (target.getUserId() != null) {
            keyBuilder.append(target.getUserId());
        }
        if (target.getGuildId() != null) {
            keyBuilder.append(target.getGuildId());
        }

        String key = keyBuilder.toString();
        if (shouldRefresh(key)) {
            HashMap<Integer, SettingValue> values = SettingRepository.db().getSettingValues(target, category);
            lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
            cachedValues.put(key, values);
            return values;
        }

        return cachedValues.computeIfAbsent(key, (c) -> new HashMap<>());
    }

}
