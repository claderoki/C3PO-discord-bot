package com.c3po.listener;

import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.Mode;
import com.c3po.processors.Processor;
import com.c3po.processors.message.GuildRewardProcessor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class MessageCreateListener implements EventListener<MessageCreateEvent> {
    private final ArrayList<Processor<MessageCreateEvent>> processors = new ArrayList<>();

    @Autowired
    private GuildRewardProcessor guildRewardProcessor;

    @PostConstruct
    public void postConstruct() {

        processors.add(guildRewardProcessor);

        if (Configuration.instance().getMode().equals(Mode.PRODUCTION)) {
            processors.add(guildRewardProcessor);
        }
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    public Mono<Void> run(MessageCreateEvent event) {
        if (event.getMember().isEmpty() || event.getMember().get().isBot()) {
            return Mono.empty();
        }

        return Flux.fromIterable(processors)
            .filter((c) -> c.shouldProcess(event))
            .flatMap((c) -> c.execute(event))
            .then()
        ;
    }

    public Mono<Void> execute(MessageCreateEvent event) {
        try {
            return run(event);
        } catch (Exception e) {
            LogHelper.log(e);
            return Mono.empty();
        }
    }

}
