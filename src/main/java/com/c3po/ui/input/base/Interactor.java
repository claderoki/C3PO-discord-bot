package com.c3po.ui.input.base;

import com.c3po.core.SimpleMessage;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.core.spec.InteractionFollowupCreateMono;
import discord4j.core.spec.InteractionReplyEditMono;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Setter
public class Interactor {
    private final Event event;
    private boolean ephemeral = false;
    @Getter
    private boolean isReplied = false;

    private HashMap<String, List<Message>> followups = new HashMap<>();

    public Interactor(DeferrableInteractionEvent event) {
        this.event = event;
    }

    public Interactor(ComponentInteractionEvent event) {
        this.event = event;
    }

    private InteractionApplicationCommandCallbackReplyMono _reply() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.reply().withEphemeral(ephemeral);
        } else if (event instanceof DeferrableInteractionEvent e) {
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
        } else if (event instanceof DeferrableInteractionEvent e) {
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
            .then(Mono.fromRunnable(() -> setReplied(true)));
    }

    private Mono<Message> getEditor(SimpleMessage simpleMessage) {
        return editReply().withEmbedsOrNull(simpleMessage.getEmbed().map(List::of).orElse(null))
            .withContentOrNull(simpleMessage.getContent().orElse(null));
    }

    private Mono<Message> getReplier(SimpleMessage simpleMessage) {
        var reply = reply();
        simpleMessage.getContent().ifPresent(reply::withContent);
        simpleMessage.getEmbed().ifPresent(reply::withEmbeds);
        return reply.onErrorResume(e -> getReplier(simpleMessage).then())
            .then(Mono.fromRunnable(() -> setReplied(true)));
    }

    private Mono<Message> getFollowup(SimpleMessage simpleMessage) {
        var followup = _followup();
        simpleMessage.getContent().ifPresent(followup::withContent);
        simpleMessage.getEmbed().ifPresent(followup::withEmbeds);
        return followup.then(Mono.fromRunnable(() -> setReplied(true)));
    }

    public Mono<Message> replyOrEdit(SimpleMessage simpleMessage) {
        return isReplied ? getEditor(simpleMessage) : getReplier(simpleMessage);
    }

    public Mono<Message> followup(SimpleMessage simpleMessage) {
        return getFollowup(simpleMessage);
    }

    public Mono<Message> replyOrFollowup(SimpleMessage simpleMessage) {
        return isReplied ? getFollowup(simpleMessage) : getReplier(simpleMessage);
    }

    private InteractionFollowupCreateMono _followup() {
        if (event instanceof ComponentInteractionEvent e) {
            return e.createFollowup();
        } else if (event instanceof DeferrableInteractionEvent e) {
            return e.createFollowup();
        }
        throw new RuntimeException("Can't happen.");
    }

    public Mono<Message> followup(Function<InteractionFollowupCreateMono, InteractionFollowupCreateMono> func, Integer maxOnScreen, String key) {
        return Mono.defer(() -> {
            if (maxOnScreen != null) {
                var messages = followups.get(key);
                if (messages != null && messages.size() >= maxOnScreen) {
                    return messages.get(messages.size()-1).delete();
                }
            }
            return Mono.empty();
        }).then(func.apply(_followup())
            .map(m -> {
                followups.computeIfAbsent(key, a -> new ArrayList<>()).add(m);
                return m;
            }));
    }

    public Mono<Message> followup(Function<InteractionFollowupCreateMono, InteractionFollowupCreateMono> func) {
        return followup(func, null, "global");
    }

}
