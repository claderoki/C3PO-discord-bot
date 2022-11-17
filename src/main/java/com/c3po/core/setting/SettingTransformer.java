package com.c3po.core.setting;

import com.c3po.command.SettingInfo;
import com.c3po.core.command.CommandManager;
import com.c3po.helper.DiscordCommandOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

import java.util.Collection;
import java.util.List;

public class SettingTransformer {
    public static final String viewOptionName = "viewsettings";
    public static final String configOptionName = "config";

    private static void hydrateFinalSettingOption(ImmutableApplicationCommandOptionData.Builder builder, Setting setting) {
        builder.name(setting.getKey().replace("_id", ""));
        builder.description("Choose something.");
        builder.required(true);
        switch (setting.getType()) {
            case INTEGER -> {
                builder.type(DiscordCommandOptionType.INTEGER.getValue());
                builder.minValue(1D);
                builder.maxValue(99999D);
            }
            case STRING -> builder.type(DiscordCommandOptionType.STRING.getValue());
            case BOOLEAN -> builder.type(DiscordCommandOptionType.BOOLEAN.getValue());
            case CHANNEL -> {
                builder.type(DiscordCommandOptionType.CHANNEL.getValue());
                builder.addAllChannelTypes(List.of(0));
            }
            case CATEGORY -> {
                builder.type(DiscordCommandOptionType.CHANNEL.getValue());
                builder.addAllChannelTypes(List.of(4));
            }
            case ROLE -> builder.type(DiscordCommandOptionType.ROLE.getValue());

        }
    }

    private static String formatDescription(String description) {
        if (description == null) {
            return "No description";
        }
        if (description.length() > 100) {
            return description.substring(0, 100);
        }
        return description;
    }

    public static ApplicationCommandRequest toCommand(String category, Collection<Setting> settings, CommandManager commandManager) {
        ImmutableApplicationCommandRequest.Builder request = ApplicationCommandRequest.builder();
        request.name(category);
        request.description("General " + category + " config");

        for (Setting setting: settings) {
            String name = "set" + setting.getKey()
                .replace("_id", "")
                .replace("_", "");

            // TODO: move side effect here that adds data to the command listener to somewhere more logical.
            commandManager.register(category + " " + name, new SettingInfo(category, setting.getKey()));
            ImmutableApplicationCommandOptionData.Builder finalOption = ApplicationCommandOptionData.builder();
            hydrateFinalSettingOption(finalOption, setting);

            request.addOption(ApplicationCommandOptionData.builder()
                .type(DiscordCommandOptionType.SUB_COMMAND.getValue())
                .description(formatDescription(setting.getDescription()))
                .name(name)
                .addOption(finalOption.build())
                .build());
        }

        request.addOption(ApplicationCommandOptionData.builder()
            .type(DiscordCommandOptionType.SUB_COMMAND.getValue())
            .description("View current settings")
            .name(viewOptionName)
            .build());

        // TODO: move side effect here that adds data to the command listener to somewhere more logical.
        commandManager.register(category + " " + viewOptionName, new SettingInfo(category, viewOptionName));

        return request.build();
    }
}
