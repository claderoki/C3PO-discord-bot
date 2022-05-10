package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.Player;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class HangmanPlayer extends Player {
    private final User user;
    private final int humanId;
    private final List<Guess> guesses = new ArrayList<>();
    private boolean dead = false;
    private int bet = 0;

    public void addGuess(Guess guess) {
        guesses.add(guess);
    }

    public int getPercentageGuessed(int total) {
        float totalWorth = guesses.stream().mapToInt(Guess::getWorth).sum();
        return (int)(totalWorth / (float)total * 100.0);
    }
}
