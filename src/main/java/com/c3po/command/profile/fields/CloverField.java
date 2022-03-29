package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;

public class CloverField extends ProfileField<Long> {
    public CloverField(Long value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode("\uD83C\uDF40");
    }

    @Override
    public String getValue() {
        return value.toString();
    }
}
