package com.c3po.processors.attribute;

import com.c3po.helper.cache.CacheManager;
import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class CacheClearer extends Task {
    @Override
    public Mono<Void> execute(GatewayDiscordClient client) {
        return Mono.fromRunnable(CacheManager::removeAllExpiredItems);
    }

    @Override
    public Duration getDelay() {
        return Duration.ofMinutes(10);
    }
}
