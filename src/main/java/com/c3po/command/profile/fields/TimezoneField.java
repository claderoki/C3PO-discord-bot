package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimezoneField extends ProfileField<ZoneId> {
    public TimezoneField(ZoneId value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode("\uD83D\uDD51");
    }

    @Override
    public String getValue() {
        return LocalTime.now(value).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
