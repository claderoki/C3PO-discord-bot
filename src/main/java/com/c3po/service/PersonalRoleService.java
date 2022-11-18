package com.c3po.service;

import com.c3po.command.personalrole.PersonalRoleProcessor;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.setting.SettingCategory;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.PersonalRolePositionKey;
import com.c3po.helper.cache.keys.PersonalRoleSettingsKey;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.personalrole.PersonalRoleSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class PersonalRoleService extends BaseSettingService<PersonalRoleSettings> {
    private final AttributeRepository attributeRepository;

    public PersonalRoleService(SettingService settingService, SettingRepository settingRepository, AttributeRepository attributeRepository) {
        super(settingService, settingRepository);
        this.attributeRepository = attributeRepository;
    }

    public int getRolePosition(Guild guild) {
        Integer position = CacheManager.get().computeIfAbsent(new PersonalRolePositionKey(ScopeTarget.guild(guild.getId().asLong())), (key) -> {
            String value = attributeRepository.getOldestValueFor(guild.getId().asLong(), PersonalRoleProcessor.personalRoleAttributeId);
            Role role = guild.getRoleById(Snowflake.of(value)).block();
            return role == null ? null : role.getRawPosition();
        });
        return position == null ? 0 : position;
    }

    @Override
    protected SettingCategory getCategory() {
        return SettingCategory.PERSONALROLE;
    }

    @Override
    protected PersonalRoleSettings getBaseSettings(ScopeTarget target) {
        return new PersonalRoleSettings(target);
    }

    @Override
    protected SettingGroupCacheKey<PersonalRoleSettings> getCacheKey(ScopeTarget target) {
        return new PersonalRoleSettingsKey(target);
    }
}
