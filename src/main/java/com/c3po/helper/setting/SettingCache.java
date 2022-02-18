package com.c3po.helper.setting;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class SettingCache extends Cache<Setting> {
    private static final HashMap<String, HashMap<String, Setting>> cache = new HashMap<>();
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();

    private static HashMap<String, Setting> getUncached(String category) throws SQLException {
        HashMap<String, Setting> settings = SettingRepository.db().getSettings(category);
        lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
        cache.put(category, settings);
        return settings;
    }

    private static boolean shouldRefresh(String key) {
        OffsetDateTime lastRefresh = lastRefreshes.get(key);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static HashMap<String, Setting> get(String category) throws SQLException {
        if (shouldRefresh(category)) {
            return getUncached(category);
        }
        return cache.computeIfAbsent(category, (c) -> new HashMap<>());
    }

    public static void clear(String category) {
        lastRefreshes.remove(category);
        cache.remove(category);
    }

}
