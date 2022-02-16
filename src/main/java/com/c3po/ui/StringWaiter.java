package com.c3po.ui;

import com.c3po.errors.PublicException;
import discord4j.core.object.command.Interaction;

public class StringWaiter extends Waiter<String> {
    private Interaction interaction;
    private String value;

    public StringWaiter(Interaction interaction) {
        super(interaction);
    }

    @Override
    protected String parse(String content) throws PublicException {
        return content;
    }

    @Override
    protected void setValue(String value) {
        String a = "";
    }

}
