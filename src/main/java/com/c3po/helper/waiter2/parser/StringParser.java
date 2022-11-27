package com.c3po.helper.waiter2.parser;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class StringParser extends MessageContentParser<String> {
    @Override
    protected Mono<Void> validateValue(String value) {
        return Mono.empty();
    }

    @Override
    protected Mono<String> parseContent(String content) {
        return Mono.just(content);
    }
}
