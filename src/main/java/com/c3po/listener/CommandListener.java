package com.c3po.listener;

import com.c3po.command.Command;
import com.c3po.command.CommandSettingValidation;
import com.c3po.command.SettingGroup;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandListener {
    private final static List<Command> commands = new ArrayList<>(){{
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
                    if (CommandSettingValidation.validate(command.getSettings(), event)) {
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