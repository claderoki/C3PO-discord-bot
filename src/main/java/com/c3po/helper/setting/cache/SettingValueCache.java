package com.c3po.helper.setting.cache;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.OldCache;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class SettingValueCache extends OldCache<SettingValue> {
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();
    private static final HashMap<String, HashMap<Integer, SettingValue>> cache = new HashMap<>();

    private static boolean shouldRefresh(String key) {
        OffsetDateTime lastRefresh = lastRefreshes.get(key);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static HashMap<Integer, SettingValue> get(SettingScopeTarget target, String category) throws SQLException {
        String key = category + target;
        if (shouldRefresh(key)) {
            HashMap<Integer, SettingValue> values = SettingRepository.db().getSettingValues(target, category);
            lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
            cache.put(key, values);
            return values;
        }
        return cache.computeIfAbsent(key, (c) -> new HashMap<>());
    }

    public static void clear(SettingScopeTarget target, String category) {
        String key = category + target;
        lastRefreshes.remove(key);
        cache.remove(key);
    }

}
