package com.c3po.command.snakeoil.game.card;

import com.c3po.game.card.Card;
import lombok.Getter;

@Getter
public class Word extends Card<String> {
    public Word(String value) {
        super(value);
    }
}
