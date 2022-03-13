package com.c3po.core.command;

import com.c3po.core.command.option.OptionContainer;
import com.c3po.helper.EventHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.Getter;

@Getter
public class Context {
    private final ChatInputInteractionEvent event;
    private final OptionContainer options;

    public Context(ChatInputInteractionEvent event) {
        this.event = event;
        this.options = EventHelper.getOptionContainer(event);
    }

}
