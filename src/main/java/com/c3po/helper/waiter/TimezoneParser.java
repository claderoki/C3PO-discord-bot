package com.c3po.helper.waiter;

import com.c3po.error.PublicException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;

@Builder
@Getter
@Deprecated
public class TimezoneParser extends MessageParser<ZoneId> {
    @Override
    protected ZoneId parseValue(ParseResult<ZoneId> result, MessageCreateEvent event) {
        try {
            return ZoneId.of(event.getMessage().getContent());
        } catch (Exception e) {
            throw new PublicException("Timezone not found.");
        }
    }

    @Override
    protected void validateValue(ParseResult<ZoneId> result, ZoneId value) {}

    @Override
    public String getPromptFooter() {
        return "Europe/Amsterdam";
    }
}
