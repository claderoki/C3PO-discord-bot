package com.c3po.service;

import com.c3po.model.milkyway.MilkywayItem;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.MilkywayIdentifierKey;
import com.c3po.helper.cache.keys.MilkywaySettingsKey;
import com.c3po.core.setting.KnownCategory;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.model.milkyway.MilkywaySettings;

import java.util.List;

public class MilkywayService {
    public static MilkywaySettings getSettings(ScopeTarget target) {
        return Cache.computeIfAbsent(new MilkywaySettingsKey(target), key -> {
            MilkywaySettings settings = new MilkywaySettings(target);
            for(PropertyValue value: SettingRepository.db().getHydratedPropertyValues(target, KnownCategory.MILKYWAY).values()) {
                String settingKey = SettingService.getCode(value.getParentId());
                settings.set(settingKey, value.getValue());
            }
            return settings;
        });
    }

    public static long getIncrementIdentifier(long guildId) {
        MilkywayIdentifierKey key = new MilkywayIdentifierKey(ScopeTarget.guild(guildId));
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
