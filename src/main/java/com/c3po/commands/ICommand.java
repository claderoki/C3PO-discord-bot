package com.c3po.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface ICommand {

    String getName();

    Mono<Void> handle(ChatInputInteractionEvent event);
}