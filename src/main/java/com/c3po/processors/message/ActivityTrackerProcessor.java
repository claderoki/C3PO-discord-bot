package com.c3po.processors.message;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.property.PropertyValue;
import com.c3po.database.SQLRuntimeException;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.LogHelper;
import com.c3po.helper.LogScope;
import com.c3po.model.guildreward.ActivityTrackerSettings;
import com.c3po.processors.Processor;
import com.c3po.service.ActivityTrackerService;
import com.c3po.service.AttributeService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ActivityTrackerProcessor extends Processor<MessageCreateEvent> {
    private final static Duration COOLDOWN_PERIOD = Duration.ofSeconds(10);

    private final AttributeRepository attributeRepository;
    private final AttributeService attributeService;
    private final ActivityTrackerService activityTrackerService;

    private static Integer attributeId = null;

    private int getAttributeId() {
        if (attributeId == null) {
            attributeId = attributeService.getId(KnownAttribute.lastActive);
        }
        return attributeId;
    }

    public boolean shouldProcess(MessageCreateEvent event) {
        return event.getGuildId().isPresent() && event.getMember().isPresent();
    }

    private Mono<Void> resetLastActive(ScopeTarget target) {
        PropertyValue value = attributeService.getAttributeValue(target, getAttributeId());
        value.setValue(DateTimeHelper.now().format(DateTimeHelper.DATETIME_FORMATTER));
        try {
            attributeRepository.save(value);
        } catch (SQLRuntimeException e) {
            String a = "";
        }
//        LogHelper.log("Reset activity for %s".formatted(target), LogScope.DEVELOPMENT);
        return Mono.empty();
    }

    public Mono<Void> execute(MessageCreateEvent event) {
        long userId = event.getMember().orElseThrow().getId().asLong();
        long guildId = event.getGuildId().orElseThrow().asLong();

        ActivityTrackerSettings settings = activityTrackerService.getSettings(ScopeTarget.guild(guildId));

        if (!settings.isEnabled()) {
            return Mono.empty();
        }
        return resetLastActive(ScopeTarget.member(userId, guildId));
    }
}
