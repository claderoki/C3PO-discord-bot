package com.c3po.command.hangman.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Guess {
    private final GuessType type;
    private final String value;
    @Setter
    private int worth = 0;
}
