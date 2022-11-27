package com.c3po.helper.waiter;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Deprecated
public class IntParser extends MessageParser<Integer> {
    private Integer min;
    private Integer max;

    public String getPromptFooter() {
        return "min: " + min + " max:" + max;
    }

    @Override
    protected Integer parseValue(ParseResult<Integer> result, MessageCreateEvent event) {
        Message message = event.getMessage();
        try {
            return Integer.parseInt(message.getContent());
        } catch (NumberFormatException e) {
            result.addError("This is not a number.");
            return null;
        }
    }

    @Override
    protected void validateValue(ParseResult<Integer> result, Integer value) {
        if (min != null && value < min) {
            result.addError("Value can't be less than " + min);
        }
        if (max != null && value > max) {
            result.addError("Value can't be more than " + max);
        }
    }
}
