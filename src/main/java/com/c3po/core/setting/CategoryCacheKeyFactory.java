package com.c3po.core.setting;

import com.c3po.core.ScopeTarget;
import com.c3po.helper.cache.keys.*;

public class CategoryCacheKeyFactory {
    public static SettingGroupCacheKey<?> create(SettingCategory category, ScopeTarget target) {
        return switch (category) {
            case GUILDREWARDS -> new GuildRewardSettingsKey(target);
            case MILKYWAY -> new MilkywaySettingsKey(target);
            case PERSONALROLE -> new PersonalRoleSettingsKey(target);
            case DISCONNECTER -> new DisconnecterSettingsKey(target);
            case ACTIVITY_TRACKER -> new ActivityTrackerSettingsKey(target);
        };
    }
}
