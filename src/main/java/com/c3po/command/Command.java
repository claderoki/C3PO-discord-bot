package com.c3po.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public abstract String getName();

    public CommandSettings getSettings() {
        return null;
    }

    public abstract Mono<Void> handle(ChatInputInteractionEvent event) throws Exception;

    public ArrayList<String> getRequiredSettings() {
        return new ArrayList<>();
    }

}
