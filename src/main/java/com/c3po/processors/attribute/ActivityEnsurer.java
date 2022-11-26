package com.c3po.processors.attribute;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.helper.DateTimeHelper;
import com.c3po.service.ActivityTrackerService;
import com.c3po.service.AttributeService;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ActivityEnsurer extends AttributeEnsurer {
    ActivityTrackerService activityTrackerService;

    public ActivityEnsurer(AttributeRepository attributeRepository, AttributeService attributeService) {
        super(attributeRepository, attributeService);
    }

    @Override
    protected String getAttributeCode() {
        return KnownAttribute.lastActive;
    }

    @Override
    protected Mono<Boolean> shouldExecute(Guild guild) {
        return Mono.just(activityTrackerService.getSettings(ScopeTarget.guild(guild.getId())).isEnabled());
    }

    @Override
    protected String getValue(Member member) {
        return DateTimeHelper.now().format(DateTimeHelper.DATETIME_FORMATTER);
    }
}
