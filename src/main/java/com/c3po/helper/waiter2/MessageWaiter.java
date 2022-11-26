package com.c3po.helper.waiter2;

import com.c3po.ui.input.base.Interactor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class MessageWaiter<T> extends Waiter<T, MessageCreateEvent> {
    public MessageWaiter(Interaction interaction, Interactor interactor) {
        super(interaction, interactor);
    }

    @Override
    protected Mono<Boolean> isAllowed(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().isEmpty()) {
            return Mono.just(true);
        }

        boolean sameUser = interaction.getUser().equals(message.getAuthor().get());
        boolean sameChannel = interaction.getChannelId().equals(message.getChannelId());
        return Mono.just(sameUser && sameChannel);
    }

    @Override
    protected Class<MessageCreateEvent> getEventClass() {
        return MessageCreateEvent.class;
    }
}
