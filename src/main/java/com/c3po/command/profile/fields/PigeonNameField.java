package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;

public class PigeonNameField extends ProfileField<String> {
    public PigeonNameField(String value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.of(767362416941203456L, "pigeon", false);
    }

    @Override
    public String getValue() {
        return value;
    }
}
