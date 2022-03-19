package com.c3po.helper.waiter;

import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class MessageParser<T> extends EventParser<T, MessageCreateEvent> {

    protected void finish(MessageCreateEvent event) {
        event.getMessage().delete().subscribe();
    }

    @Override
    protected boolean preValidate(MessageCreateEvent event) {
        return true;
    }

}
