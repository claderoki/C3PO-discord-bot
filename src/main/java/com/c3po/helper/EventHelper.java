package com.c3po.helper;

import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.option.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

import java.util.HashMap;
import java.util.Map;

public class EventHelper {

    public static ScopeTarget scopeToTarget(Scope scope, ChatInputInteractionEvent event) {
        return switch (scope) {
            case GUILD -> ScopeTarget.guild(event.getInteraction().getGuildId().orElseThrow().asLong());
            case USER -> ScopeTarget.user(event.getInteraction().getUser().getId().asLong());
            case MEMBER -> ScopeTarget.member(
                event.getInteraction().getUser().getId().asLong(),
                event.getInteraction().getGuildId().orElseThrow().asLong()
            );
        };
    }

    public static OptionContainer getOptionContainer(ChatInputInteractionEvent event) {
        return new OptionContainer(getOptionsFromEvent(event));
    }

    protected static Map<String, CommandOption<?>> getOptionsFromEvent(ChatInputInteractionEvent event) {
        HashMap<String, CommandOption<?>> options = new HashMap<>();
        for (ApplicationCommandInteractionOption option: event.getOptions()) {
            hydrateCommandOptions(option, options);
        }
        return options;
    }

    protected static CommandOption<?> getCommandOption(ApplicationCommandInteractionOption option) {
        String key = option.getName();
        if (option.getValue().isEmpty()) {
            return null;
        }
        ApplicationCommandInteractionOptionValue raw = option.getValue().orElseThrow();
        return switch (option.getType()) {
            case INTEGER -> new LongOption(key, raw);
            case BOOLEAN -> new BoolOption(key, raw);
            case USER, CHANNEL, ROLE, MENTIONABLE -> new SnowflakeOption(key, raw);
            case NUMBER -> new DoubleOption(key, raw);
            default -> new StringOption(key, raw);
        };
    }

    protected static void hydrateCommandOptions(ApplicationCommandInteractionOption option, HashMap<String, CommandOption<?>> options) {
        switch (option.getType()) {
            case SUB_COMMAND, SUB_COMMAND_GROUP -> {
                for (ApplicationCommandInteractionOption subOption : option.getOptions()) {
                    hydrateCommandOptions(subOption, options);
                }
            }
            default -> options.put(option.getName(), getCommandOption(option));
        }
    }

}
