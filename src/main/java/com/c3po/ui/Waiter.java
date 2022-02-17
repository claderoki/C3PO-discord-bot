package com.c3po.ui;

import com.c3po.errors.PublicException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.Interaction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public abstract class Waiter<T> {
    private Interaction interaction;
    private T value;

    public Waiter(Interaction interaction) {
        this.interaction = interaction;
    }

    protected boolean isAllowed(MessageCreateEvent event) {
        if (event.getMember().isPresent()) {
            return interaction.getUser().getId().equals(event.getMember().get().getId());
        } else {
            return true;
        }
    }

    protected abstract T parse(String content) throws PublicException;

    public Mono<Void> handle() throws Exception {
        return interaction.getClient().on(MessageCreateEvent.class, messageCreateEvent -> {
                    if (!isAllowed(messageCreateEvent)) {
                        return Mono.empty();
                    }

                    try {
                        setValue(parse(messageCreateEvent.getMessage().getContent()));
                        return Mono.error(new TimeoutException());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    messageCreateEvent.getMessage().delete().then();

                    return Mono.empty();

                }).timeout(Duration.ofSeconds(10))
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .then();
    }

    protected void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
