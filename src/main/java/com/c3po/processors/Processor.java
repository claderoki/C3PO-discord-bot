package com.c3po.processors;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public abstract class Processor<T extends Event> {
    public abstract boolean shouldProcess(T event);
    public abstract Mono<Void> execute(T event);
}
