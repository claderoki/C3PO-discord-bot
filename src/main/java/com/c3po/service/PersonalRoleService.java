package com.c3po.service;

import com.c3po.command.personalrole.PersonalRoleProcessor;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.core.setting.KnownCategory;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.GuildRewardSettingsKey;
import com.c3po.helper.cache.keys.PersonalRolePositionKey;
import com.c3po.helper.cache.keys.PersonalRoleSettingsKey;
import com.c3po.model.guildreward.GuildRewardSettings;
import com.c3po.model.personalrole.PersonalRoleSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;

public class PersonalRoleService extends Service {
    public static PersonalRoleSettings getSettings(ScopeTarget target) {
        PersonalRoleSettingsKey key = new PersonalRoleSettingsKey(target);
        PersonalRoleSettings settings = Cache.get(key);
        if (settings != null) {
            return settings;
        }

        settings = new PersonalRoleSettings(target);
        for(PropertyValue value: SettingRepository.db().getHydratedPropertyValues(target, KnownCategory.PERSONALROLE).values()) {
            String settingKey = SettingService.getCode(value.getParentId());
            settings.set(settingKey, value.getValue());
        }
        Cache.set(key, settings);
        return settings;
    }

    public static int getRolePosition(Guild guild) {
        PersonalRolePositionKey key = new PersonalRolePositionKey(ScopeTarget.guild(guild.getId().asLong()));
        Integer position = Cache.get(key);
        if (position != null) {
            return position;
        }

        String value = AttributeRepository.db().getOldestValueFor(guild.getId().asLong(), PersonalRoleProcessor.personalRoleAttributeId);

        Long roleId = Long.parseLong(value);

        Role role = guild.getRoleById(Snowflake.of(value)).block();
        if (role == null) {
            return 0;
        }

        position = role.getRawPosition();
        Cache.set(key, position);

        return position;


    }
}
