package com.c3po.helper.waiter2.parser;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public abstract class MessageParser<T> extends EventParser<T, MessageCreateEvent> {
    protected Mono<Void> finish(MessageCreateEvent event) {
        return event.getMessage().delete()
            .onErrorResume(Throwable.class, e -> Mono.empty());
    }
}
