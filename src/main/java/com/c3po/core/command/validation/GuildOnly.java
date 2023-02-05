package com.c3po.core.command.validation;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class GuildOnly extends CommandValidation {
    public final Mono<Boolean> validate(ChatInputInteractionEvent event) {
        return Mono.just(event.getInteraction().getGuildId().isPresent());
    }
}
