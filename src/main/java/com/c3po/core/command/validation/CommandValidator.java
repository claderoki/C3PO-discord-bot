package com.c3po.core.command.validation;

import com.c3po.error.PublicException;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class CommandValidator {
    public Mono<Boolean> validate(ChatInputInteractionEvent event, List<CommandValidation> validations) {
        record ValidationResult(CommandValidation validation, Boolean result) {}
        Flux<ValidationResult> values = Flux.concat(validations
            .parallelStream()
            .map(v -> v.validate(event).map(r -> new ValidationResult(v, r)))
            .toList());

        return values
            .filter(v -> !v.result)
            // TODO: combine errors into 1 since now it just stops after the first.
            .flatMap(c -> Mono.error(new PublicException("%s validation failed".formatted(c.validation.getClass().getSimpleName()))))
            .collectList()
            .map(List::isEmpty);
    }
}
