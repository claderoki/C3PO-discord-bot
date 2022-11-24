package com.c3po.processors.attribute;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.helper.LogHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class AttributePurger extends Task {
    private final AttributeRepository attributeRepository;

    public Mono<Integer> executeForGuild(Guild guild) {
        return guild.getMembers()
            .map(User::getId)
            .map(Snowflake::asLong)
            .collectList()
            .filter(c -> c.size() != 0)
            .map(HashSet::new)
            .map(i -> attributeRepository.purge(guild.getId().asLong(), i));
    }

    public Mono<Void> execute(GatewayDiscordClient client) {
        return client.getGuilds()
            .filter(c -> c.getId().equals(Snowflake.of(729843647347949638L)))
            .flatMap(this::executeForGuild)
            .filter(c -> c > 0)
            .doOnNext(i -> LogHelper.log(i + " attribute values purged"))
            .then();
    }

    @Override
    public Duration getDelay() {
        return Duration.ofHours(1);
    }
}
