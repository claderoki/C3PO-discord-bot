package com.c3po.listener;

import com.c3po.helper.LogHelper;
import com.c3po.processors.Processor;
import com.c3po.processors.message.GuildRewardProcessor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class MessageCreateListener implements EventListener<MessageCreateEvent> {
    private List<Processor<MessageCreateEvent>> processors;

    public MessageCreateListener() {
        processors = List.of(
            new GuildRewardProcessor()
        );
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    public Mono<?> run(MessageCreateEvent event) throws Exception {
        if (event.getMember().isEmpty() || event.getMember().get().isBot()) {
            return Mono.empty();
        }

        return Flux.fromIterable(processors)
            .next()
            .filter((c) -> c.shouldProcess(event))
            .flatMap((c) -> c.execute(event))
            .then()
        ;
    }

    public Mono<?> execute(MessageCreateEvent event) {
        try {
            return run(event);
        } catch (Exception e) {
            LogHelper.log(e);
            return Mono.empty();
        }
    }

}
