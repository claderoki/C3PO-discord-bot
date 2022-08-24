package com.c3po.ui.input.base;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.core.spec.InteractionReplyEditMono;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

@Setter
public class Replier {
//    private static final HashMap<String, Boolean> replies = new HashMap<>();
    private final Event event;
    private boolean ephemeral = false;
    @Getter
    private boolean isReplied = false;

    public Replier(ChatInputInteractionEvent event) {
        this.event = event;
    }

    public Replier(ComponentInteractionEvent event) {
        this.event = event;
    }

    private String getId() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.getCustomId();
        } else if (event instanceof ChatInputInteractionEvent e) {
            return e.getInteraction().getId().asString();
        }
        throw new RuntimeException("Can't happen.");
    }

    private InteractionApplicationCommandCallbackReplyMono _reply() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.reply().withEphemeral(ephemeral);
        } else if (event instanceof ChatInputInteractionEvent e) {
            return e.reply().withEphemeral(ephemeral);
        }
        throw new RuntimeException("Can't happen.");
    }

    public InteractionApplicationCommandCallbackReplyMono reply() {
        return _reply().withEphemeral(ephemeral);
    }

    public InteractionApplicationCommandCallbackReplyMono reply(Function<InteractionApplicationCommandCallbackReplyMono, InteractionApplicationCommandCallbackReplyMono> replier) {
        return replier.apply(_reply().withEphemeral(ephemeral));
    }

    private InteractionReplyEditMono _editReply() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.editReply();
        } else if (event instanceof ChatInputInteractionEvent e) {
            return e.editReply();
        }
        throw new RuntimeException("Can't happen.");
    }

    public InteractionReplyEditMono editReply() {
        return _editReply();
    }

    public InteractionReplyEditMono editReply(Function<InteractionReplyEditMono, InteractionReplyEditMono> editor) {
        return editor.apply(_editReply());
    }

    public Mono<Message> replyOrEdit(Function<InteractionApplicationCommandCallbackReplyMono, InteractionApplicationCommandCallbackReplyMono> replier,
                                     Function<InteractionReplyEditMono, InteractionReplyEditMono> editor) {
        if (isReplied) {
            return editor.apply(editReply());
        }

        return replier.apply(reply())
            .onErrorResume(c -> editor.apply(editReply()).then())
            .then(Mono.defer(() -> {
                setReplied(true);
                return Mono.empty();
            }))
            ;
    }

}
