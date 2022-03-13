package com.c3po.listener;

import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {

    Class<T> getEventType();

    Mono<?> execute(T event);

    default Mono<?> handle(T event) {
        try {
            return execute(event);
        } catch (Exception e) {
            LogHelper.log(e);
            return Mono.empty();
        }
    }

}

