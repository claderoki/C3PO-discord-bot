package com.c3po.helper.waiter2.parser;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Setter
@NoArgsConstructor
public abstract class EventParser<T, F extends Event> {
    protected ChatInputInteractionEvent event;

    protected abstract Mono<T> parseValue(F event);
    protected abstract Mono<Void> validateValue(T value);

    protected Mono<Void> finish(F event) {
        return Mono.empty();
    }

    public abstract Optional<String> getPromptFooter();

    public Mono<T> parse(F event) {
        return parseValue(event)
            .flatMap(v -> validateValue(v).then(Mono.just(v)))
            .flatMap(v -> finish(event).then(Mono.just(v)));
    }

}
