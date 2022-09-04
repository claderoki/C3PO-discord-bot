package com.c3po.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandCategory {
    GUILDREWARDS("guildrewards"),
    PROFILE("profile"),
    POLLS("poll"),
    PIGEON("pigeon"),
    MILKYWAY("milkyway"),
    HANGMAN("hangman"),
    SNAKE_OIL("snakeoil"),
    PERSONAL_ROLE("personalrole"),
    BLACKJACK("blackjack"),
    INSECTA("insecta");

    private final String value;

}
