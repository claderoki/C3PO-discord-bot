package com.c3po.core.command.validation;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class CommandValidation {
    public abstract Mono<Boolean> validate(ChatInputInteractionEvent event);
}
