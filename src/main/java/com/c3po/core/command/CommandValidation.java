package com.c3po.core.command;

import reactor.core.publisher.Mono;

public interface CommandValidation<T extends CommandValidationResult> {
    Mono<T> validate(Context context);
}
