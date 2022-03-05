package com.c3po.helper.waiter;

import com.c3po.errors.PublicException;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;


@AllArgsConstructor
public class Waiter {
    private final ChatInputInteractionEvent event;

    public <T, F extends Event> Mono<ParseResult<T>> wait(Class<F> cls, EventParser<T, F> parser) throws PublicException {
        return this.event.getClient().on(cls, event -> Mono.just(parser.parse(event)))
            .timeout(Duration.ofSeconds(30))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .filter(c -> !c.getType().equals(ResultType.SKIP))
            .next()
        ;
    }
}
