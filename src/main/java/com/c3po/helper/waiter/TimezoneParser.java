package com.c3po.helper.waiter;

import com.c3po.errors.PublicException;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;

@Builder
@Getter
public class TimezoneParser extends MessageParser<ZoneId> {
    @Override
    protected ZoneId parseValue(MessageCreateEvent event) {
        try {
            return ZoneId.of(event.getMessage().getContent());
        } catch (Exception e) {
            throw new PublicException("Timezone not found.");
        }
    }

    @Override
    protected void validateValue(ZoneId value) {}

    @Override
    public String getPromptFooter() {
        return "Europe/Amsterdam";
    }
}
