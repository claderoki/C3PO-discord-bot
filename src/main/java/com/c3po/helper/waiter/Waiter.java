package com.c3po.helper.waiter;

import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
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

    protected boolean validate(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (message.getAuthor().isPresent()) {
            User user1 = this.event.getInteraction().getUser();
            User user2 = message.getAuthor().get();

            if (user2.isBot()) {
                return false;
            }

            if (!this.event.getInteraction().getChannelId().equals(message.getChannelId())) {
                return false;
            }

            if (!user1.getId().equals(user2.getId())) {
                return false;
            }
        }
        return true;
    }

    protected boolean validate(Event event) {
        if (event instanceof MessageCreateEvent messageCreateEvent) {
            return validate(messageCreateEvent);
        } {
            return true;
        }
    }

    protected  <T, F extends Event> Mono<ParseResult<T>> _wait(Class<F> cls, EventParser<T, F> parser) {
        return this.event.getClient().on(cls)
            .filter(this::validate)
            .flatMap((c) -> Mono.just(parser.parse(c)))
            .timeout(Duration.ofSeconds(30))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .onErrorResume((e) -> {
                LogHelper.log(e);
                return Mono.empty();
            })
            .filter(c -> !c.getType().equals(ResultType.SKIP))
            .next();
    }

    public <T, F extends Event> Mono<ParseResult<T>> wait(Class<F> cls, EventParser<T, F> parser) throws PublicException {
        if (prompt != null) {
            return event.editReply()
                .withEmbeds(EmbedHelper.normal(prompt)
                    .footer(parser.getPromptFooter(), null)
                    .build())
                .withComponents()
                .onErrorResume((e) -> {
                    LogHelper.log(e);
                    return Mono.empty();
                })
                .then(_wait(cls,parser));
        } else {
            return _wait(cls,parser);
        }
    }
}