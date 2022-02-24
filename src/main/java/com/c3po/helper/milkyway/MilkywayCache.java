package com.c3po.helper.milkyway;

import com.c3po.command.milkyway.MilkywayItem;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import com.c3po.helper.cache.OldCache;
import com.c3po.helper.setting.KnownCategory;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import com.c3po.helper.setting.cache.SettingCache;
import com.c3po.model.MilkywaySettings;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

public class MilkywayCache extends OldCache<MilkywaySettings> {
    private static final HashMap<String, MilkywaySettings> settingCache = new HashMap<>();
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();

    private static boolean shouldRefresh(String cacheKey) {
        OffsetDateTime lastRefresh = lastRefreshes.get(cacheKey);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static MilkywaySettings getSettings(long guildId) throws SQLException {
        SettingScopeTarget target = SettingScopeTarget.guild(guildId);
        String category = KnownCategory.MILKYWAY;
        String cacheKey = target + category;
        if (shouldRefresh(cacheKey)) {
            MilkywaySettings settings = new MilkywaySettings(target);
            for(SettingValue value: SettingRepository.db().getHydratedSettingValues(target, category).values()) {
                String settingKey = SettingCache.getCode(value.getSettingId());
                settings.set(settingKey, value.getValue());
            }
            settingCache.put(cacheKey, settings);
            lastRefreshes.put(cacheKey, OffsetDateTime.now(ZoneOffset.UTC));
            return settings;
        }
        return settingCache.get(cacheKey);
    }

    private static TimedTrigger timedTrigger = new TimedTrigger(Duration.ofHours(10));
    private static ArrayList<MilkywayItem> itemCache = new ArrayList<>();

    public static ArrayList<MilkywayItem> getAvailableItems() {
        timedTrigger.check(() -> itemCache = MilkywayRepository.db().getAvailableItems());
        return itemCache;
    }

    public static void clear() {
        lastRefreshes.clear();
        settingCache.clear();
    }

}
