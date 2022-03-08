package com.c3po.command;

import com.c3po.command.option.OptionContainer;
import com.c3po.errors.PublicException;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public abstract class Command {

    public abstract String getName();

    public CommandSettings getSettings() {
        return null;
    }

    public abstract Mono<Void> handle(ChatInputInteractionEvent event, OptionContainer options) throws RuntimeException;

    public ArrayList<Integer> getRequiredSettings() {
        return new ArrayList<>();
    }

}
