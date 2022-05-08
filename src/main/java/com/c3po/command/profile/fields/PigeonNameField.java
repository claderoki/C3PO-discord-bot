package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;

public class PigeonNameField extends ProfileField<String> {
    public PigeonNameField(String value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode("\uD83D\uDC26");
    }

    @Override
    public String getValue() {
        return value;
    }
}
