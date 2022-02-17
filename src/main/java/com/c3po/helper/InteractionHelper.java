package com.c3po.helper;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

public class InteractionHelper {

    private static ApplicationCommandInteractionOptionValue getOptionValueFromOption(ApplicationCommandInteractionOption option, String name) {
        switch (option.getType()) {
            case SUB_COMMAND, SUB_COMMAND_GROUP -> {
                for (ApplicationCommandInteractionOption opt1: option.getOptions()) {
                    return getOptionValueFromOption(opt1, name);
                }
            }
            default -> {
                if (option.getName().equals(name) && option.getValue().isPresent()) {
                    return option.getValue().get();
                }
            }
        }
        return null;
    }

    public static ApplicationCommandInteractionOptionValue getOptionValue(ChatInputInteractionEvent event, String name) {
        for (ApplicationCommandInteractionOption a: event.getOptions()) {
            ApplicationCommandInteractionOptionValue value = getOptionValueFromOption(a, name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
