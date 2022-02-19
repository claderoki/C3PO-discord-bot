package com.c3po.helper.setting;

import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.listener.CommandListener;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

import java.util.Collection;
import java.util.List;

public class SettingTransformer {
    public static String viewOptionName = "viewsettings";

    private static void hydrateFinalSettingOption(ImmutableApplicationCommandOptionData.Builder builder, Setting setting) {
        builder.name(setting.getKey().replace("_id", ""));
        builder.description("def");
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
        }
    }

    public static ApplicationCommandRequest toCommand(String category, Collection<Setting> settings) {
        ImmutableApplicationCommandRequest.Builder request = ApplicationCommandRequest.builder();
        request.name(category);
        request.description("okok");

        for (Setting setting: settings) {
            String name = "set" + setting.getKey()
                    .replace("_id", "")
                    .replace("_", "");

            // TODO: move side effect here that adds data to the command listener to somewhere more logical.
            CommandListener.addSettingGroup(category, name, setting.getKey());
            ImmutableApplicationCommandOptionData.Builder finalOption = ApplicationCommandOptionData.builder();
            hydrateFinalSettingOption(finalOption, setting);

            request.addOption(ApplicationCommandOptionData.builder()
                    .type(DiscordCommandOptionType.SUB_COMMAND.getValue())
                    .description("abc")
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
        CommandListener.addSettingGroup(category, viewOptionName, viewOptionName);

        return request.build();
    }
}
