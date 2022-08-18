package com.c3po.command.snakeoil.game.card;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Card<V> {
    protected final V value;
}
