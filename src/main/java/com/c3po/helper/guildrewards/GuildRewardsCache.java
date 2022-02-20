package com.c3po.helper.guildrewards;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.setting.KnownCategory;
import com.c3po.helper.setting.Setting;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import com.c3po.helper.setting.cache.SettingCache;
import com.c3po.model.GuildRewardsSettings;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class GuildRewardsCache extends Cache<GuildRewardsSettings> {
    private static final HashMap<String, GuildRewardsSettings> cache = new HashMap<>();
    private static final HashMap<String, OffsetDateTime> lastRefreshes = new HashMap<>();

    private static boolean shouldRefresh(String cacheKey) {
        OffsetDateTime lastRefresh = lastRefreshes.get(cacheKey);
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(Duration.ofHours(1)));
    }

    public static GuildRewardsSettings getSettings(long guildId) throws SQLException {
        SettingScopeTarget target = SettingScopeTarget.guild(guildId);
        String category = KnownCategory.GUILDREWARDS;
        String cacheKey = target + category;
        if (shouldRefresh(cacheKey)) {
            GuildRewardsSettings settings = new GuildRewardsSettings(target);
            for(SettingValue value: SettingRepository.db().getHydratedSettingValues(target, category).values()) {
                String settingKey = SettingCache.getCode(value.getSettingId());
                settings.set(settingKey, value.getValue());
            }
            cache.put(cacheKey, settings);
            lastRefreshes.put(cacheKey, OffsetDateTime.now(ZoneOffset.UTC));
            return settings;
        }
        return cache.get(cacheKey);
    }

    public static void clear() {
        lastRefreshes.clear();
        cache.clear();
    }

}
