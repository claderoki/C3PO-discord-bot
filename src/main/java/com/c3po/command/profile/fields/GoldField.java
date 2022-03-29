package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;

public class GoldField extends ProfileField<Long> {
    public GoldField(Long value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode("\uD83D\uDCB6");
    }

    @Override
    public String getValue() {
        return value.toString();
    }
}
