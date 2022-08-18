package com.c3po.command.snakeoil.game.card;

public class Word extends Card<String> {
    private final String description;

    public Word(String value, String description) {
        super(value);
        this.description = description;
    }
}
