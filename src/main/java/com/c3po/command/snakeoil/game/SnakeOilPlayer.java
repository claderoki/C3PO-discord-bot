package com.c3po.command.snakeoil.game;

import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class SnakeOilPlayer {
    private final User user;
    private final Deck deck;
    private int score = 0;
    private boolean isKing;
    private TurnStatus turnStatus;

    public void incrementScore() {
        score++;
    }
}
