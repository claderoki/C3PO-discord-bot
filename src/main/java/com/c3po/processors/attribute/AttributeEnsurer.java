package com.c3po.processors.attribute;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.Attribute;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.LogHelper;
import com.c3po.service.AttributeService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Ensures every member of every guild has a value for the chosen attribute.
 */
@Component
@RequiredArgsConstructor
public abstract class AttributeEnsurer extends Task {
    private final AttributeRepository attributeRepository;
    private final AttributeService attributeService;

    protected abstract String getAttributeCode();
    protected abstract String getValue(Member member);

    public int getAttributeId() {
        return attributeService.getId(getAttributeCode());
    }

    private Mono<Integer> executeForGuild(Guild guild) {
        Set<Long> userIds = attributeRepository.getUserIdsHaving(guild.getId().asLong(), getAttributeId());
        Attribute attribute = attributeRepository.getAttribute(getAttributeId());

        return guild.getMembers()
            .filter(m -> !userIds.contains(m.getId().asLong()))
            .map(m -> PropertyValue.builderFrom(attribute)
                .value(getValue(m))
                .target(ScopeTarget.member(m.getId(), guild.getId()))
                .build())
            .collectList()
            .filter(c -> !c.isEmpty())
            .doOnSuccess(c -> attributeRepository.save(c.toArray(PropertyValue[]::new)))
            .map(List::size);
    }

    public Mono<Void> execute(GatewayDiscordClient client) {
        return client.getGuilds()
            .filter(c -> c.getId().equals(Snowflake.of(729843647347949638L)))
            .flatMap(this::executeForGuild)
            .filter(c -> c > 0)
            .doOnNext(i -> LogHelper.log(i + " attribute(s) created."))
            .then();
    }

    @Override
    public Duration getDelay() {
        return Duration.ofHours(1);
    }
}
