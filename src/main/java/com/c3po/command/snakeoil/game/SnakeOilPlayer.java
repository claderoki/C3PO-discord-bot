package com.c3po.command.snakeoil.game;

import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SnakeOilPlayer {
    private final User user;
    private final Deck deck;
}
