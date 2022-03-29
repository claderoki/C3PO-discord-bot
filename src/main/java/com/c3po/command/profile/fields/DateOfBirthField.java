package com.c3po.command.profile.fields;

import com.c3po.helper.DateTimeHelper;
import discord4j.core.object.reaction.ReactionEmoji;

import java.time.LocalDate;
import java.time.Period;

public class DateOfBirthField extends ProfileField<LocalDate> {
    public DateOfBirthField(LocalDate value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode("\uD83C\uDF82");
    }

    @Override
    public String getValue() {
        return String.valueOf(Period.between(value, DateTimeHelper.now().toLocalDate()).getYears());
    }
}
