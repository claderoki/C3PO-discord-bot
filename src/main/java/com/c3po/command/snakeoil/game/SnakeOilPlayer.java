package com.c3po.command.snakeoil.game;

import com.c3po.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Word;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class SnakeOilPlayer {
    private final User user;
    private final Deck<Word> words;
    private int score = 0;
    private TurnStatus turnStatus;
    private PlayerStatus status;

    public void incrementScore() {
        score++;
    }

    public boolean equals(SnakeOilPlayer other) {
        return this.user.equals(other.getUser());
    }

    @Override
    public String toString() {
        return user.getUsername();
    }
}
