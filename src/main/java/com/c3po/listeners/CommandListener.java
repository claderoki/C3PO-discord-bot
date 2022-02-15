package com.c3po.listeners;
import com.c3po.commands.ICommand;
import com.c3po.commands.guildrewards.GuildRewardsSetupCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandListener {
    private final static List<ICommand> commands = new ArrayList<>(){{
        add(new GuildRewardsSetupCommand());
    }};

    private static String getFullyQualifiedCommandName(ChatInputInteractionEvent event) {
        StringBuilder base = new StringBuilder(event.getCommandName());
        for (ApplicationCommandInteractionOption option: event.getOptions()) {
            switch (option.getType()) {
                case SUB_COMMAND, SUB_COMMAND_GROUP -> base.append(" ").append(option.getName());
            }
        }
        return base.toString();
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        String fullyQualifiedCommandName = getFullyQualifiedCommandName(event);
        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(fullyQualifiedCommandName))
                .next()
                .flatMap(command -> command.handle(event));
    }
}