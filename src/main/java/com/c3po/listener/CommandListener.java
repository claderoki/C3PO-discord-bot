package com.c3po.listener;

import com.c3po.command.Command;
import com.c3po.command.CommandSettings;
import com.c3po.command.guildrewards.GuildRewardsSetMaxPointsCommand;
import com.c3po.command.guildrewards.GuildRewardsSetMinPointsCommand;
import com.c3po.command.guildrewards.GuildRewardsToggleCommand;
import com.c3po.command.milkyway.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.ImmutableMemberData;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandListener {
    private final static List<Command> commands = new ArrayList<>(){{
        add(new GuildRewardsSetMinPointsCommand());
        add(new GuildRewardsSetMaxPointsCommand());
        add(new GuildRewardsToggleCommand());

        add(new MilkywayToggleCommand());
        add(new MilkywaySetLogChannelCommand());
        add(new MilkywaySetCategoryCommand());
        add(new MilkywaySetLimitCommand());
        add(new MilkywaySetCostPerDayCommand());
        add(new MilkywayGodmodeCommand());
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

    private static boolean validateSettings(CommandSettings commandSettings, ChatInputInteractionEvent event) {
        if (commandSettings == null) {
            return true;
        }
        if (commandSettings.isAdminOnly() && event.getInteraction().getMember().isPresent()) {
            PermissionSet permissions = event.getInteraction().getMember().get().getBasePermissions().block();
            if (permissions == null || !permissions.contains(Permission.ADMINISTRATOR)) {
                return false;
            }
        }
        if (commandSettings.isGuildOnly() && event.getInteraction().getGuildId().isEmpty()) {
            return false;
        }

        return true;
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        String fullyQualifiedCommandName = getFullyQualifiedCommandName(event);

        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(fullyQualifiedCommandName))
                .next()
                .flatMap(command -> {
                    if (validateSettings(command.getSettings(), event)) {
                        try {
                            return command.handle(event);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
            });
    }
}