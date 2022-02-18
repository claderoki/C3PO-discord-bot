package com.c3po.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class Command {

    public abstract String getName();

    public boolean validate(ChatInputInteractionEvent event) {
        return true;
    }


    public abstract Mono<Void> handle(ChatInputInteractionEvent event) throws Exception;
}
