package com.c3po.listener;

import com.c3po.command.Command;
import com.c3po.command.CommandSettings;
import com.c3po.command.SettingGroup;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandListener {
    private final static List<Command> commands = new ArrayList<>(){{
//        add(new GuildRewardsSetMinPointsCommand());
//        add(new GuildRewardsSetMaxPointsCommand());
//        add(new GuildRewardsToggleCommand());
//
//        add(new MilkywayToggleCommand());
//        add(new MilkywaySetLogChannelCommand());
//        add(new MilkywaySetCategoryCommand());
//        add(new MilkywaySetLimitCommand());
//        add(new MilkywaySetCostPerDayCommand());
//        add(new MilkywayGodmodeCommand());
    }};

    private final static HashMap<String, HashMap<String, String>> settingMap = new HashMap<>();

    public static void addSettingGroup(String category, String optionName, String settingKey) {
        settingMap.computeIfAbsent(category, c -> new HashMap<>()).put(optionName, settingKey);
    }

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
        HashMap<String, String> settings = settingMap.get(event.getCommandName());
        if (settings != null) {
            for(ApplicationCommandInteractionOption option: event.getOptions()) {
                String settingKey = settings.get(option.getName());
                if (settingKey != null) {
                    String category = event.getCommandName();
                    SettingGroup settingGroup = new SettingGroup(category, settingKey);
                    try {
                        return settingGroup.handle(event);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Mono.empty();
                    }
                }
            }
        }

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