package com.c3po.helper.waiter;

import com.c3po.error.PublicException;
import com.c3po.helper.EmbedHelper;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
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
            // testing
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
        } else {
            return true;
        }
    }

    private <T> Possible<T> toPossible(Optional<T> option) {
        return option.map(Possible::of).orElseGet(Possible::absent);
    }

    private EmbedCreateSpec.Builder copyEmbed(Embed embed) {
        return EmbedCreateSpec.builder()
            .color(toPossible(embed.getColor()))
            .fields(embed.getFields().stream().map((f) -> EmbedCreateFields.Field.of(f.getName(), f.getValue(), f.isInline())).toList())
            .footer(embed.getFooter().map(c -> EmbedCreateFields.Footer.of(c.getText(), c.getIconUrl().orElse(null))).orElse(null))
        ;
    }

    protected  <T, F extends Event> Mono<ParseResult<T>> _wait(Class<F> cls, EventParser<T, F> parser) {
        return this.event.getClient().on(cls)
            .filter(this::validate)
            .flatMap((c) -> Mono.just(parser.parse(c)))
            .timeout(Duration.ofSeconds(120))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .flatMap(c -> {
                if (c.getType().equals(ResultType.ERROR)) {
                    List<String> errors = c.getErrors();
                    if (!errors.isEmpty()) {
                        return event.editReply()
                            .withEmbedsOrNull(List.of(EmbedHelper.error("Error(s): " + String.join(", ", errors)).build()))
                            .delayElement(Duration.ofSeconds(3))
                            .then(sendMessage(parser))
                            .then(Mono.just(c));
                    }
                }
                return Mono.just(c);
            })
            .filter(c -> c.getType().equals(ResultType.SUCCESS))
            .next();
    }

    private <T, F extends Event> Mono<Void> sendMessage(EventParser<T, F> parser) {
        if (prompt == null) {
            return Mono.empty();
        }
        String footer = parser.getPromptFooter();
        return event.editReply()
            .withEmbedsOrNull(List.of(EmbedHelper.normal(prompt)
                .footer(footer == null ? null : EmbedCreateFields.Footer.of(footer, null))
                .build()))
            .withComponentsOrNull(null)
            .then()
        ;
    }

    public <T, F extends Event> Mono<ParseResult<T>> wait(Class<F> cls, EventParser<T, F> parser) throws PublicException {
        return sendMessage(parser).then(_wait(cls,parser));
    }
}