package com.c3po.processors.attribute;

import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AnonymousTask extends Task {
    private final String identifier;
    private final Duration delay;
    private final Function<GatewayDiscordClient, Mono<Void>> executor;

    @Override
    public Mono<Void> execute(GatewayDiscordClient client) {
        return executor.apply(client);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Duration getDelay() {
        return delay;
    }
}
