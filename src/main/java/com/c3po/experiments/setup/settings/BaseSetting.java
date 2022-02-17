package com.c3po.experiments.setup.settings;

import com.c3po.ui.Waiter;
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import reactor.core.publisher.Mono;

public abstract class BaseSetting<T> {
    abstract void hydrateValue(T value);

    InteractionApplicationCommandCallbackReplyMono hydratePrompt(InteractionApplicationCommandCallbackReplyMono reply) {
        return reply;
    }

    public Mono<Void> input(DeferrableInteractionEvent event) throws Exception {
        InteractionApplicationCommandCallbackReplyMono prompt = hydratePrompt(event.reply());
        Waiter<T> waiter = getWaiter(event);
        return prompt.then(waiter.handle().then().doFinally(c -> hydrateValue(waiter.getValue())));
    }

    abstract Waiter<T> getWaiter(DeferrableInteractionEvent event);

    void configureWaiter(Waiter<T> waiter) {

    }
}
