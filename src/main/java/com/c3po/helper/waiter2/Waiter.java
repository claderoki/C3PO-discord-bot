package com.c3po.helper.waiter2;

import com.c3po.core.SimpleMessage;
import com.c3po.helper.waiter2.parser.EventParser;
import com.c3po.helper.waiter2.parser.ParseException;
import com.c3po.ui.input.base.Interactor;
import discord4j.core.event.domain.Event;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.time.Duration;

@RequiredArgsConstructor
@Accessors(chain = true)
public abstract class Waiter<T, E extends Event> {
    protected final Interaction interaction;
    private final Interactor interactor;

    @Setter
    private @Nullable String prompt;

    private int retries = 0;

    protected abstract Mono<Boolean> isAllowed(E event);

    protected abstract Class<E> getEventClass();

    private Mono<Message> notifyError(ParseException exception) {
        return interactor.followup(f -> f.withContent(exception.getMessage()), 1, "parseException");
    }

    protected Mono<T> _wait(EventParser<T, E> parser) {
        return interaction.getClient().on(getEventClass())
            .filterWhen(this::isAllowed)
            .flatMap(parser::parse)
            .timeout(Duration.ofSeconds(120))
            .onErrorResume(ParseException.class, e -> Mono.fromRunnable(() -> retries++)
                .then(notifyError(e))
                .then(Mono.empty())
            )
            .next();
    }

    private Mono<Message> sendMessage(EventParser<T, E> parser) {
        if (prompt == null) {
            return Mono.empty();
        }
        var embed = EmbedCreateSpec.create()
            .withDescription(prompt);
        if (parser.getPromptFooter().isPresent()) {
            embed = embed.withFooter(EmbedCreateFields.Footer.of(parser.getPromptFooter().get(), null));
        }
        return interactor.replyOrEdit(SimpleMessage.builder()
            .embed(embed)
            .build());
    }

    public Mono<T> wait(EventParser<T, E> parser) {
        return sendMessage(parser).then(_wait(parser));
    }
}