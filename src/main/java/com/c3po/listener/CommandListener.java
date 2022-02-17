package com.c3po.listener;

import com.c3po.command.ICommand;
import com.c3po.command.guildrewards.GuildRewardsSetMinPointsCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandListener {
    private final static List<ICommand> commands = new ArrayList<>(){{
        add(new GuildRewardsSetMinPointsCommand());
    }};


    private static void appendOptionFullyQualifiedCommandName(ApplicationCommandInteractionOption option, StringBuilder builder) {
        switch (option.getType()) {
            case SUB_COMMAND -> builder.append(" ").append(option.getName());
            case SUB_COMMAND_GROUP -> {
                builder.append(" ").append(option.getName());
                for (ApplicationCommandInteractionOption op1 : option.getOptions()) {
                    appendOptionFullyQualifiedCommandName(op1, builder);
                }
            }
        }
    }

    private static String getFullyQualifiedCommandName(ChatInputInteractionEvent event) {
        StringBuilder base = new StringBuilder(event.getCommandName());
        for (ApplicationCommandInteractionOption option: event.getOptions()) {
            appendOptionFullyQualifiedCommandName(option, base);
        }
        return base.toString();
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        String fullyQualifiedCommandName = getFullyQualifiedCommandName(event);

        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(fullyQualifiedCommandName))
                .next()
                .flatMap(command -> {
                    try {
                        return command.handle(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }
}