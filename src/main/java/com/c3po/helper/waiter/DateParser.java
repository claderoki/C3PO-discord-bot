package com.c3po.helper.waiter;

import com.c3po.helper.DateTimeHelper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
public class DateParser extends MessageParser<LocalDate> {

    @Override
    public String getPromptFooter() {
        return "YYYY-MM-DD (example: 1994-02-06)";
    }

    @Override
    protected LocalDate parseValue(ParseResult<LocalDate> result, MessageCreateEvent event) {
        return LocalDate.parse(event.getMessage().getContent(), DateTimeHelper.DATE_FORMATTER);
    }

    @Override
    protected void validateValue(ParseResult<LocalDate> result, LocalDate value) {}
}
