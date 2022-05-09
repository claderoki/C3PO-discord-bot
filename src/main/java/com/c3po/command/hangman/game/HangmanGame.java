package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.Game;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.Emoji;
import com.c3po.helper.LogHelper;
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
        int betPool = players.stream().mapToInt(HangmanPlayer::getBet).sum();
        betPool *= 1.25;
        betPool += (15 * players.size());

//        players.sort(Comparator.comparingInt(player -> {
//            if (player.isDead()) {
//                return 0;
//            }
//            return player.getGuesses().stream().mapToInt(Guess::getWorth).sum();
//        }));

        List<String> lines = new ArrayList<>();
        int i = 0;
        for(var player: players) {
            String name = player.getUser().getMention();
            if (player.isDead()) {
                HumanRepository.db().decreaseGold(player.getHumanId(), player.getBet());
                lines.add("%s\nLost %s **%s**".formatted(name, Emoji.EURO, player.getBet()));
                continue;
            }

            int percentageGuessed = player.getPercentageGuessed(word.getValue().length());
            int won = (int)Math.ceil(percentageGuessed / 100.0 * betPool);

            int nextWon = 0;
            if (players.size() > 1 && i == 0) {
                HangmanPlayer nextPlayer = players.get(1);
                nextWon = (int)Math.ceil(nextPlayer.getPercentageGuessed(word.getValue().length()) / 100.0 * betPool);
            }
            if (won > nextWon) {
                name += " 🌟";
            }

            int totalWorth = player.getGuesses().stream().mapToInt(Guess::getWorth).sum();

            HumanRepository.db().increaseGold(player.getHumanId(), won);
            lines.add("%s\nWon %s **%s** (%s/%s) guessed".formatted(name, Emoji.EURO, won, totalWorth, word.getValue().length()));
            i++;
        }

        var embed = EmbedHelper.normal(String.join("\n", lines));
        embed.addField(word.getValue(), word.getDescription() == null ? "no definition" : word.getDescription(), false);
        embed.title("Game has ended.");
        ui.showEndGame(embed.build());
    }

    public void start() {
        for (int i = 0; i < word.getValue().length(); i++) {
            board.add(EMPTY_LETTER);
        }
        LogHelper.log(word.getValue());

        List<HangmanPlayer> livingPlayers = new ArrayList<>(players);
        List<Guess> allGuesses = new ArrayList<>();
        ui.showBoard(board, players, null);
        boolean gameEnded = false;
        while (!gameEnded) {
            for(HangmanPlayer player: livingPlayers) {
                long incorrectGuesses = player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
                if (incorrectGuesses >= HangmanStateHelper.getMaxStates()) {
                    livingPlayers.remove(player);
                    player.setDead(true);
                } else {
                    ui.showBoard(board, players, player);
                    Guess guess = ui.waitForGuess(player, board, allGuesses);
                    if (guess != null) {
                        processGuess(guess);
                        player.addGuess(guess);
                        allGuesses.add(guess);
                    }
                }
                gameEnded = livingPlayers.isEmpty() || board.stream().noneMatch(c -> c.equals(EMPTY_LETTER));
                if (gameEnded) {
                    break;
                }
            }
        }
        ui.showBoard(board, players, null);
        stop();
    }
}
