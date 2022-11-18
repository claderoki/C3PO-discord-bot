package com.c3po.service;

import com.c3po.helper.cache.CacheManager;
import com.c3po.model.milkyway.MilkywayItem;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.cache.keys.MilkywayIdentifierKey;
import com.c3po.helper.cache.keys.MilkywaySettingsKey;
import com.c3po.core.setting.KnownCategory;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.model.milkyway.MilkywaySettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilkywayService {
    private final SettingService settingService;
    private final SettingRepository settingRepository;
    private final MilkywayRepository milkywayRepository;

    public MilkywaySettings getSettings(ScopeTarget target) {
        return CacheManager.get().computeIfAbsent(new MilkywaySettingsKey(target), key -> {
            MilkywaySettings settings = new MilkywaySettings(target);
            for(PropertyValue value: settingRepository.getHydratedPropertyValues(target, KnownCategory.MILKYWAY).values()) {
                String settingKey = settingService.getCode(value.getParentId());
                settings.set(settingKey, value.getValue());
            }
            return settings;
        });
    }

    public long getIncrementIdentifier(long guildId) {
        MilkywayIdentifierKey key = new MilkywayIdentifierKey(ScopeTarget.guild(guildId));
        Long identifier = CacheManager.get().get(key);
        if (identifier != null) {
            CacheManager.get().set(key, ++identifier);
            return identifier;
        }

        identifier = milkywayRepository.getIncrementIdentifier(guildId);
        CacheManager.get().set(key, identifier);
        return identifier;
    }

    public List<MilkywayItem> getItems() {
        return List.of(
            MilkywayItem.builder().itemId(33).itemName("Milkyway").emoji("\uD83C\uDF0C").daysWorth(7).build(),
            MilkywayItem.builder().itemId(54).itemName("Orion's belt").emoji("\uD83C\uDF20").daysWorth(1).build()
        );
    }

}
