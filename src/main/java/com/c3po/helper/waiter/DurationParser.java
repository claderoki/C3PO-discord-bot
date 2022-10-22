package com.c3po.helper.waiter;

import com.c3po.helper.DurationFormatter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Builder
@Getter
public class DurationParser extends MessageParser<Duration> {
    @Override
    protected Duration parseValue(ParseResult<Duration> result, MessageCreateEvent event) {
        String content = event.getMessage().getContent();
        return DurationFormatter.parse(content);
    }

    @Override
    protected void validateValue(ParseResult<Duration> result, Duration value) {

    }

    @Override
    public String getPromptFooter() {
        return "2 days";
    }
}
