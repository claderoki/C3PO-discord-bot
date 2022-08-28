package com.c3po.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandCategory {
    GUILDREWARDS("guildrewards"),
    PROFILE("profile"),
    POLLS("polls"),
    PIGEON("pigeon"),
    MILKYWAY("milkyway"),
    HANGMAN("hangman"),
    SNAKE_OIL("snakeoil"),
    PERSONAL_ROLE("personalrole"),
    ;

    private final String value;

}
