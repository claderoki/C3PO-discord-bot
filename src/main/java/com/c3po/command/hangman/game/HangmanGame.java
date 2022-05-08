package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.Game;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HangmanGame extends Game {
    private final HangmanWord word;
    private final List<HangmanPlayer> players;
    private final HangmanUI ui;
    private final List<Character> board = new ArrayList<>();
    private final static char EMPTY_LETTER = '-';

    private void processGuess(Guess guess) {
        if (guess.getType().equals(GuessType.WORD) && guess.getValue().equals(word.getValue())) {
            guess.setWorth((int) (board.stream().filter(c -> c.equals(EMPTY_LETTER)).count() - word.getValue().length()));
            for (int i = 0; i < word.getValue().length(); i++) {
                board.set(i, word.getValue().charAt(i));
            }
        } else if (guess.getType().equals(GuessType.LETTER) && word.getValue().contains(guess.getValue())) {
            char guessedLetter = guess.getValue().charAt(0);
            int worth = 0;
            for(int i = 0; i < word.getValue().length(); i++) {
                char letter = word.getValue().charAt(i);
                if (letter == guessedLetter) {
                    worth++;
                    board.set(i, letter);
                }
            }
            guess.setWorth(worth);
        }
    }

    private void stop() {

    }

    public void start() {
        for (int i = 0; i < word.getValue().length(); i++) {
            board.add(EMPTY_LETTER);
        }

        List<HangmanPlayer> livingPlayers = new ArrayList<>(players);
        ui.showBoard(board, players, null);
        while (!livingPlayers.isEmpty() && board.stream().anyMatch(c -> c.equals(EMPTY_LETTER))) {
            for(HangmanPlayer player: livingPlayers) {
                long incorrectGuesses = player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
                if (incorrectGuesses >= HangmanUI.states.length) {
                    livingPlayers.remove(player);
                    player.setDead(true);
                } else {
                    ui.showBoard(board, players, player);
                    Guess guess = ui.waitForGuess(player, board);
                    if (guess != null) {
                        processGuess(guess);
                        player.addGuess(guess);
                    }
                }
            }
        }
        ui.showBoard(board, players, null);
        stop();
    }
}
