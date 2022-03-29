package com.c3po.command.profile.fields;

import discord4j.core.object.reaction.ReactionEmoji;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ProfileField<T> {
    protected final T value;

    public abstract ReactionEmoji getEmoji();
    protected abstract String getValue();

    public String getParsedValue() {
        if (value == null) {
            return "N/A";
        } else {
            return getValue();
        }
    }

    public boolean isVisible() {
        return value != null;
    }
}
