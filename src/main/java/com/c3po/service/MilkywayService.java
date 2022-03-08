package com.c3po.service;

import com.c3po.model.milkyway.MilkywayItem;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.MilkywayIdentifierKey;
import com.c3po.helper.cache.keys.MilkywaySettingsKey;
import com.c3po.helper.setting.KnownCategory;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import com.c3po.model.milkyway.MilkywaySettings;

import java.util.List;

public class MilkywayService {
    public static MilkywaySettings getSettings(SettingScopeTarget target) {
        MilkywaySettingsKey key = new MilkywaySettingsKey(target);
        MilkywaySettings settings = Cache.get(key);
        if (settings != null) {
            return settings;
        }

        settings = new MilkywaySettings(target);
        for(SettingValue value: SettingRepository.db().getHydratedSettingValues(target, KnownCategory.MILKYWAY).values()) {
            String settingKey = SettingService.getCode(value.getSettingId());
            settings.set(settingKey, value.getValue());
        }
        Cache.set(key, settings);
        return settings;
    }

    public static long getIncrementIdentifier(long guildId) {
        MilkywayIdentifierKey key = new MilkywayIdentifierKey(SettingScopeTarget.guild(guildId));
        Long identifier = Cache.get(key);
        if (identifier != null) {
            Cache.set(key, ++identifier);
            return identifier;
        }

        identifier = MilkywayRepository.db().getIncrementIdentifier(guildId);
        Cache.set(key, identifier);
        return identifier;
    }

    public static List<MilkywayItem> getItems() {
        return List.of(
            MilkywayItem.builder().itemId(33).itemName("Milkyway").emoji("\uD83C\uDF0C").daysWorth(7).build(),
            MilkywayItem.builder().itemId(54).itemName("Orion's belt").emoji("\uD83C\uDF20").daysWorth(1).build()
        );
    }

}
