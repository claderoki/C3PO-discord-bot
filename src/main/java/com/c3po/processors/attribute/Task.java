package com.c3po.processors.attribute;

import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public abstract class Task {
    public abstract Mono<Void> execute(GatewayDiscordClient client);

    public abstract Duration getDelay();
}
