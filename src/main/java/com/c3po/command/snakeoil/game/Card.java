package com.c3po.command.snakeoil.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Card {
    private final String word;
    private boolean selected = false;
}
