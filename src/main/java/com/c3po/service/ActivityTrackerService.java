package com.c3po.service;

import com.c3po.activitytracker.InactiveMember;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.property.AttributeCondition;
import com.c3po.core.setting.SettingCategory;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.cache.keys.ActivityTrackerSettingsKey;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.model.guildreward.ActivityTrackerSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class ActivityTrackerService extends BaseSettingService<ActivityTrackerSettings> {
    private final AttributeService attributeService;
    private final AttributeRepository attributeRepository;

    public ActivityTrackerService(SettingService settingService, SettingRepository settingRepository, AttributeService attributeService, AttributeRepository attributeRepository) {
        super(settingService, settingRepository);
        this.attributeService = attributeService;
        this.attributeRepository = attributeRepository;
    }

    @Override
    protected SettingCategory getCategory() {
        return SettingCategory.ACTIVITY_TRACKER;
    }

    @Override
    protected ActivityTrackerSettings getBaseSettings(ScopeTarget target) {
        return new ActivityTrackerSettings(target);
    }

    @Override
    protected SettingGroupCacheKey<ActivityTrackerSettings> getCacheKey(ScopeTarget target) {
        return new ActivityTrackerSettingsKey(target);
    }

    public Flux<InactiveMember> getInactiveMembers(Guild guild, Duration cutOffDuration) {
        return Flux.fromStream(attributeRepository.queryCondition(
                    guild.getId().asLong(),
                    attributeService.getId(KnownAttribute.lastActive),
                    AttributeCondition.LTE,
                    DateTimeHelper.now().minus(cutOffDuration).format(DateTimeHelper.DATETIME_FORMATTER)
                )
                .entrySet()
                .stream()
                .map(c -> guild.getMemberById(Snowflake.of(c.getKey()))
                    .map(m -> new InactiveMember(m, LocalDateTime.parse(c.getValue(), DateTimeHelper.DATETIME_FORMATTER)))))
            .flatMap(Function.identity());
    }


}
