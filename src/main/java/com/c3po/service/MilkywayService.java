package com.c3po.service;

import com.c3po.command.milkyway.MilkywayItem;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.MilkywaySettingsKey;
import com.c3po.helper.setting.KnownCategory;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import com.c3po.model.MilkywaySettings;

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

    public static List<MilkywayItem> getItems() {
        return List.of(
            MilkywayItem.builder().itemId(33).itemName("Milkyway").daysWorth(7).build(),
            MilkywayItem.builder().itemId(54).itemName("Orion's belt").daysWorth(1).build()
        );
    }

}
