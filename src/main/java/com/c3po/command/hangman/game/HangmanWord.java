package com.c3po.command.hangman.game;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HangmanWord {
    private String value;
    private String description;
}
