package com.c3po.processors.message;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.LogHelper;
import com.c3po.helper.LogScope;
import com.c3po.model.guildreward.GuildRewardSettings;
import com.c3po.processors.Processor;
import com.c3po.service.AttributeService;
import com.c3po.service.GuildRewardService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Random;

public class GuildRewardProcessor extends Processor<MessageCreateEvent> {
    private final AttributeRepository attributeRepository = AttributeRepository.db();
    private final static AttributeService attributeService = new AttributeService();

    private static final HashMap<String, OffsetDateTime> lastRewards = new HashMap<>();
    private static final int attributeId = attributeService.getId(KnownAttribute.CLOVERS);
    private final GuildRewardService guildRewardService = new GuildRewardService();

    public boolean shouldProcess(MessageCreateEvent event) {
        return event.getGuildId().isPresent() && event.getMember().isPresent();
    }

    protected boolean shouldReward(ScopeTarget target, Duration timeout) {
        OffsetDateTime lastRefresh = lastRewards.get(target.toString());
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(timeout));
    }

    protected int getPointsToReward(GuildRewardSettings settings) {
        int minPoints = settings.getMinPointsPerMessage();
        int maxPoints = settings.getMaxPointsPerMessage();
        if (minPoints == maxPoints) {
            return minPoints;
        }

        Random r = new Random();
        return r.ints(minPoints, maxPoints).findFirst().orElseThrow();
    }

    public Mono<Void> execute(MessageCreateEvent event) {
        long userId = event.getMember().orElseThrow().getId().asLong();
        long guildId = event.getGuildId().orElseThrow().asLong();

        ScopeTarget target = ScopeTarget.member(userId, guildId);
        GuildRewardSettings settings = guildRewardService.getSettings(ScopeTarget.guild(guildId));

        if (!settings.isEnabled()) {
            return Mono.empty();
        }

        if (shouldReward(target, settings.getTimeout())) {
            PropertyValue value = attributeService.getAttributeValue(target, attributeId);
            int pointsToReward = getPointsToReward(settings);
            value.increment(pointsToReward);
            attributeRepository.save(value);
            LogHelper.log("Gave %s points to %s".formatted(pointsToReward, target), LogScope.DEVELOPMENT);
            lastRewards.put(target.toString(), DateTimeHelper.offsetNow());
        }

        return Mono.empty();
    }

}
