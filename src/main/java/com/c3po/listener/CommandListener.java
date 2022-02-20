package com.c3po.listener;

import com.c3po.command.Command;
import com.c3po.command.CommandSettingValidation;
import com.c3po.command.SettingGroup;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.setting.*;
import com.c3po.helper.setting.cache.SettingCache;
import com.c3po.model.GuildRewardsSettings;
import com.c3po.ui.table.Row;
import com.c3po.ui.table.Table;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private static String valuesToView(HashMap<Integer, SettingValue> settingValues) {
        StringBuilder builder = new StringBuilder();
        for (SettingValue settingValue: settingValues.values()) {
            String key = SettingCache.getCode(settingValue.getSettingId());
            builder.append(key)
                    .append("\t\t\t")
                    .append(DataFormatter.prettify(settingValue.getType(), settingValue.getValue()))
                    .append("\n");
        }
        return builder.toString();
    }

    private static Table valuesToTable(HashMap<Integer, SettingValue> settingValues) {
        Table table = new Table();

        for (SettingValue settingValue: settingValues.values()) {
            String key = SettingCache.getCode(settingValue.getSettingId());
            table.addRow(new Row(key, DataFormatter.prettify(settingValue.getType(), settingValue.getValue())));
        }

        return table;
    }

    private static Mono<Void> subhandle(ChatInputInteractionEvent event) throws Exception {
        HashMap<String, String> settings = settingMap.get(event.getCommandName());
        if (settings != null) {
            for(ApplicationCommandInteractionOption option: event.getOptions()) {
                String settingKey = settings.get(option.getName());
                String category = event.getCommandName();
                if (settingKey == null) {
                    break;
                }

                if (settingKey.equals(SettingTransformer.viewOptionName)) {
                    SettingScopeTarget target = SettingGroup.scopeToTarget(SettingScope.GUILD, event);
                    HashMap<Integer, SettingValue> values = SettingRepository.db().getHydratedSettingValues(target, category);
//                    Table table = valuesToTable(values);
                    String content = valuesToView(values);
                    return event.reply().withContent("```\n%s```".formatted(content));
                } else {
                    SettingGroup settingGroup = new SettingGroup(category, settingKey);
                    return settingGroup.handle(event);
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
                    return Mono.empty();
                });
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        try {
            return subhandle(event);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }
}