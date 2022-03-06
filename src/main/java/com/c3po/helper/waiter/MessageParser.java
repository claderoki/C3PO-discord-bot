package com.c3po.helper.waiter;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public abstract class MessageParser<T> extends EventParser<T, MessageCreateEvent> {

    protected void finish(MessageCreateEvent event) {
        event.getMessage().delete().subscribe();
    }

    @Override
    protected boolean preValidate(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (message.getAuthor().isPresent()) {
            User user1 = this.event.getInteraction().getUser();
            User user2 = message.getAuthor().get();

            if (!this.event.getInteraction().getChannelId().equals(message.getChannelId())) {
                return false;
            }

            if (!user1.getId().equals(user2.getId())) {
                return false;
            }
        }

        return true;

    }

}
