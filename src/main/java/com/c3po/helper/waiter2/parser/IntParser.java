package com.c3po.helper.waiter2.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Setter
@Getter
@Accessors(chain = true)
public class IntParser extends MessageContentParser<Integer> {
    private Integer min;
    private Integer max;

    @Override
    protected Mono<Integer> parseContent(String content) {
        try {
            return Mono.just(Integer.parseInt(content));
        } catch (NumberFormatException e) {
            return Mono.error(new ParseException("This is not a number."));
        }
    }

    @Override
    protected Mono<Void> validateValue(Integer value) {
        ArrayList<String> errors = new ArrayList<>();
        if (min != null && value < min) {
            errors.add("Value can't be less than " + min);
        }
        if (max != null && value > max) {
            errors.add("Value can't be more than " + max);
        }
        if (!errors.isEmpty()) {
            return Mono.error(new ParseException(errors));
        }
        return Mono.empty();
    }

    public Optional<String> getPromptFooter() {
        return Optional.of("min: " + min + " max:" + max);
    }
}
