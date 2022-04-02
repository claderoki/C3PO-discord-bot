package com.c3po.helper.waiter;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public abstract class EventParser<T, F extends Event> {
    protected ChatInputInteractionEvent event;

    protected abstract T parseValue(ParseResult<T> result, F event);
    protected abstract void validateValue(ParseResult<T> result, T value);
    protected void finish(F event) {}

    public abstract String getPromptFooter();

    public ParseResult<T> parse(F event) {
        ParseResult<T> result = new ParseResult<>();
        T value = parseValue(result, event);

        if (value != null) {
            result.setValue(value);
            validateValue(result, value);
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
