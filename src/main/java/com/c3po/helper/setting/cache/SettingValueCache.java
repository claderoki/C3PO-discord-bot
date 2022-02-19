package com.c3po.helper.setting.cache;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class SettingValueCache extends Cache<SettingValue> {
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();
    private static final HashMap<String, HashMap<Integer, SettingValue>> cache = new HashMap<>();

    private static boolean shouldRefresh(String key) {
        OffsetDateTime lastRefresh = lastRefreshes.get(key);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static HashMap<Integer, SettingValue> get(SettingScopeTarget target, String category) throws SQLException {
        String key = getCacheKey(target, category);
        if (shouldRefresh(key)) {
            HashMap<Integer, SettingValue> values = SettingRepository.db().getSettingValues(target, category);
            lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
            cache.put(key, values);
            return values;
        }

        return cache.computeIfAbsent(key, (c) -> new HashMap<>());
    }

    private static String getCacheKey(SettingScopeTarget target, String category) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(category);
        if (target.getUserId() != null) {
            keyBuilder.append(target.getUserId());
        }
        if (target.getGuildId() != null) {
            keyBuilder.append(target.getGuildId());
        }

        return keyBuilder.toString();
    }

    public static void clear(SettingScopeTarget target, String category) {
        String key = getCacheKey(target, category);
        lastRefreshes.remove(key);
        cache.remove(key);
    }

}
