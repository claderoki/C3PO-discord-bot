package com.c3po.service;

import com.c3po.command.personalrole.PersonalRoleProcessor;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.core.setting.KnownCategory;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.PersonalRolePositionKey;
import com.c3po.helper.cache.keys.PersonalRoleSettingsKey;
import com.c3po.model.personalrole.PersonalRoleSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import lombok.NonNull;

public class PersonalRoleService extends Service {
    public static PersonalRoleSettings getSettings(ScopeTarget target) {
        return Cache.computeIfAbsent(new PersonalRoleSettingsKey(target), (key) -> {
            PersonalRoleSettings settings = new PersonalRoleSettings(target);
            for(PropertyValue value: SettingRepository.db().getHydratedPropertyValues(target, KnownCategory.PERSONALROLE).values()) {
                String settingKey = SettingService.getCode(value.getParentId());
                settings.set(settingKey, value.getValue());
            }
            return settings;
        });
    }

    public static int getRolePosition(Guild guild) {
        Integer position = Cache.computeIfAbsent(new PersonalRolePositionKey(ScopeTarget.guild(guild.getId().asLong())), (key) -> {
            String value = AttributeRepository.db().getOldestValueFor(guild.getId().asLong(), PersonalRoleProcessor.personalRoleAttributeId);
            Role role = guild.getRoleById(Snowflake.of(value)).block();
            return role == null ? null : role.getRawPosition();
        });
        return position == null ? 0 : position;
    }
}
