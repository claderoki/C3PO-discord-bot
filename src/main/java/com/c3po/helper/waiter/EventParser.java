package com.c3po.helper.waiter;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public abstract class EventParser<T, F extends Event> {
    protected ChatInputInteractionEvent event;

    protected final ParseResult<T> result = new ParseResult<>();

    protected abstract boolean preValidate(F event);
    protected abstract T parseValue(F event);
    protected abstract void validateValue(T value);
    protected void finish(F event) {

    }

    public abstract String getPromptFooter();

    public ParseResult<T> parse(F event) {
        if (!preValidate(event)) {
            result.setType(ResultType.SKIP);
            return result;
        }

        T value = parseValue(event);

        if (value != null) {
            result.setValue(value);
            validateValue(value);
        }

        if (!result.getErrors().isEmpty()) {
            result.setType(ResultType.ERROR);
        } else {
            result.setType(ResultType.SUCCESS);
        }

        finish(event);
        return result;
    }

}
