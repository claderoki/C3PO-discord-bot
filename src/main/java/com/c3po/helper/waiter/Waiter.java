package com.c3po.helper.waiter;

import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Setter
public class Waiter {
    private String prompt;
    private final ChatInputInteractionEvent event;
    public Waiter(ChatInputInteractionEvent event) {
        this.event = event;
    }

    public <T, F extends Event> Mono<ParseResult<T>> wait(Class<F> cls, EventParser<T, F> parser) throws PublicException {
        if (prompt != null) {
            return event.editReply()
                .withEmbeds(EmbedHelper.normal(prompt)
                    .footer(parser.getPromptFooter(), null)
                    .build())
                .withComponents().then(this.event.getClient().on(cls, event -> Mono.just(parser.parse(event)))
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                    .filter(c -> !c.getType().equals(ResultType.SKIP))
                    .next());
        } else {
            return this.event.getClient().on(cls, event -> Mono.just(parser.parse(event)))
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .filter(c -> !c.getType().equals(ResultType.SKIP))
                .next()
                ;
        }
    }
}
