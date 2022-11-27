package com.c3po.helper.waiter2.parser;

import com.c3po.helper.waiter2.RangeFilter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Setter
@Getter
@Accessors(chain = true)
public abstract class MessageContentParser<T> extends MessageParser<T> {
    private RangeFilter contentRangeFilter;

    @Override
    protected Mono<T> parseValue(MessageCreateEvent event) {
        String content = event.getMessage().getContent();
        return preValidate(content)
            .flatMap((v) -> parseContent(content));
    }

    protected Mono<Void> preValidate(String content) {
        ArrayList<String> errors = new ArrayList<>();
        if (contentRangeFilter.isTooLow(content.length())) {
            errors.add("Content can't be less than " + contentRangeFilter.getMin());
        }
        if (contentRangeFilter.isTooHigh(content.length())) {
            errors.add("Content can't be higher than " + contentRangeFilter.getMax());
        }
        if (!errors.isEmpty()) {
            return Mono.error(new ParseException(errors));
        }
        return Mono.empty();
    }

    protected abstract Mono<T> parseContent(String content);

    @Override
    public Optional<String> getPromptFooter() {
        return Optional.empty();
    }
}
