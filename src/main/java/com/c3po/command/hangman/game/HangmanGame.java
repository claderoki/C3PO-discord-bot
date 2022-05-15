package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.Game;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.Emoji;
import com.c3po.helper.LogHelper;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class HangmanGame extends Game {
    private final HangmanWord word;
    private final List<HangmanPlayer> players;
    private final HangmanUI ui;
    private final List<Character> board = new ArrayList<>();
    private final static char EMPTY_LETTER = '-';
    private List<HangmanPlayer> livingPlayers;
    private final List<Guess> allGuesses = new ArrayList<>();

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

    private Mono<?> stop() {
        int betPool = players.stream().mapToInt(HangmanPlayer::getBet).sum();
        betPool *= 1.25;
        betPool += (15 * players.size());

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
        return ui.showEndGame(embed.build());
    }

    private Mono<?> showBoard(HangmanPlayer player) {
        return ui.showBoard(board, players, player);
    }

    private Mono<Void> processPlayer(HangmanPlayer player) {
        long incorrectGuesses = player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
        if (incorrectGuesses >= HangmanStateHelper.getMaxStates()) {
            livingPlayers.remove(player);
            player.setDead(true);
            return Mono.empty();
        } else {
            return showBoard(player)
                .then(ui.waitForGuess(player, board, allGuesses)
                .flatMap(guess -> {
                    if (guess != null) {
                        processGuess(guess);
                        player.addGuess(guess);
                        allGuesses.add(guess);
                    }
                    return Mono.empty();
            }));
        }
    }

    public Mono<?> start() {
        for (int i = 0; i < word.getValue().length(); i++) {
            board.add(EMPTY_LETTER);
        }
        AtomicInteger i = new AtomicInteger();
        livingPlayers = new ArrayList<>(players);
        return Flux.fromIterable(Iterables.cycle(players))
            .filter(player -> !player.isDead())
//            .takeWhile(c -> !livingPlayers.isEmpty() && board.stream().noneMatch(l->l.equals(EMPTY_LETTER) && i.getAndIncrement() < 3))
            .takeWhile(c -> i.getAndIncrement() < 2)
            .flatMap(this::processPlayer).then(
//            .then(showBoard(null)
//                .then(stop())
            );
    }
}
