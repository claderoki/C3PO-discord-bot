package com.c3po.ui;


import com.c3po.errors.PublicException;
import discord4j.core.object.command.Interaction;

public class IntWaiter extends Waiter<Integer> {
    public IntWaiter(Interaction interaction) {
        super(interaction);
    }

    @Override
    protected Integer parse(String content) throws PublicException {
        try {
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            throw new PublicException("Value is not valid.");
        }
    }
}
