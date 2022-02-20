package com.c3po.helper.setting.cache;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.Setting;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class SettingCache extends Cache<Setting> {
    private static final HashMap<String, HashMap<Integer, Setting>> cache = new HashMap<>();
    private static HashMap<String, HashMap<String, Integer>> ids = new HashMap<>();
    private static HashMap<Integer, String> codes = new HashMap<>();

    private static final TimedTrigger idRefreshes = new TimedTrigger(Duration.ofHours(1));
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();

    private static HashMap<Integer, Setting> getUncached(String category) throws SQLException {
        HashMap<Integer, Setting> settings = SettingRepository.db().getSettings(category);
        lastRefreshes.put(category, OffsetDateTime.now(ZoneOffset.UTC));
        cache.put(category, settings);
        return settings;
    }

    private static boolean shouldRefresh(String key) {
        OffsetDateTime lastRefresh = lastRefreshes.get(key);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static HashMap<Integer, Setting> get(String category) throws SQLException {
        if (shouldRefresh(category)) {
            return getUncached(category);
        }
        return cache.computeIfAbsent(category, (c) -> new HashMap<>());
    }

    private static void refreshIdAndCodes() {
        try {
            ids = SettingRepository.db().getSettingIdentifiers();
            codes = new HashMap<>();
            for (Map.Entry<String, HashMap<String, Integer>> entry: ids.entrySet()) {
                for (Map.Entry<String, Integer> mapping: entry.getValue().entrySet()) {
                    codes.put(mapping.getValue(), mapping.getKey());
                }
            }
        } catch (SQLException e) {
            LogHelper.logException(e);
        }
    }

    public static Integer getId(String category, String key) {
        idRefreshes.check(SettingCache::refreshIdAndCodes);
        return ids.get(category).get(key);
    }

    public static String getCode(Integer id) {
        idRefreshes.check(SettingCache::refreshIdAndCodes);
        return codes.get(id);
    }

    public static void clear(String category) {
        lastRefreshes.remove(category);
        cache.remove(category);
        ids.clear();
        codes.clear();
    }

}
