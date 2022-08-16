package com.c3po.ui.input.base;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.core.spec.InteractionReplyEditMono;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter

public class Replier {
    private final Event event;
    private boolean ephemeral = false;
    @Getter
    private boolean isReplied = false;

    public InteractionApplicationCommandCallbackReplyMono reply() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.reply().withEphemeral(ephemeral);
        } else if (event instanceof ChatInputInteractionEvent e) {
            return e.reply().withEphemeral(ephemeral);
        }
        return null;
    }

    public InteractionReplyEditMono editReply() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.editReply();
        } else if (event instanceof ChatInputInteractionEvent e) {
            return e.editReply();
        }
        return null;
    }
}
