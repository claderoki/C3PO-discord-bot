package com.c3po.command.hangman.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class GameState {
    private final List<Guess> guesses = new ArrayList<>();
    private final List<Character> board = new ArrayList<>();
    private final List<HangmanPlayer> players;
    private HangmanPlayer currentPlayer;
    private int turn = 0;

    public void incrementTurn() {
        turn++;
    }
}
