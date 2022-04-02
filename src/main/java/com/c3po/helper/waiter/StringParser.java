package com.c3po.helper.waiter;

import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StringParser extends MessageParser<String> {
    private Integer min;
    private Integer max;

    @Override
    protected String parseValue(ParseResult<String> result, MessageCreateEvent event) {
        return event.getMessage().getContent();
    }

    @Override
    protected void validateValue(ParseResult<String> result, String value) {
        if (min != null && value.length() < min) {
            result.addError("Value can't be less than " + min);
        }
        if (max != null && value.length() > max) {
            result.addError("Value can't be more than " + max);
        }
    }

    @Override
    public String getPromptFooter() {
        return null;
    }
}
