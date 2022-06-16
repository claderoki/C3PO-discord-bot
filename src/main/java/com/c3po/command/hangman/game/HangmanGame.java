package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.Game;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.Emoji;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HangmanGame extends Game {
    private final HumanRepository humanRepository = HumanRepository.db();

    private final HangmanWord word;
    private final HangmanUI ui;
    private final GameState state;
    private final static char EMPTY_LETTER = '-';

    private Mono<Void> processGuess(Guess guess) {
        if (guess.getType().equals(GuessType.WORD) && guess.getValue().equals(word.getValue())) {
            guess.setWorth((int) (state.getBoard().stream().filter(c -> c.equals(EMPTY_LETTER)).count() - word.getValue().length()));
            for (int i = 0; i < word.getValue().length(); i++) {
                state.getBoard().set(i, word.getValue().charAt(i));
            }
        } else if (guess.getType().equals(GuessType.LETTER) && word.getValue().contains(guess.getValue())) {
            char guessedLetter = guess.getValue().charAt(0);
            int worth = 0;
            for(int i = 0; i < word.getValue().length(); i++) {
                char letter = word.getValue().charAt(i);
                if (letter == guessedLetter) {
                    worth++;
                    state.getBoard().set(i, letter);
                }
            }
            guess.setWorth(worth);
        }

        state.getCurrentPlayer().addGuess(guess);
        processPlayer(state.getCurrentPlayer());
        return Mono.empty();
    }

    private void processPlayer(HangmanPlayer player) {
        long incorrectGuesses = player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
        if (incorrectGuesses >= HangmanStateHelper.getMaxStates()) {
            player.setDead(true);
        }
    }

    private int getNextIndex(int index) {
        if (index >= state.getPlayers().size()) {
            return 0;
        } else {
            return index +1;
        }
    }

    private HangmanPlayer getNextPlayer() {
        if (state.getCurrentPlayer() == null) {
            return state.getPlayers().get(0);
        } else {
            int currentIndex = state.getPlayers().indexOf(state.getCurrentPlayer());
            for (int i = 0; i < state.getPlayers().size(); i++) {
                int nextIndex = getNextIndex(currentIndex);
                HangmanPlayer player = state.getPlayers().get(nextIndex);
                if (!player.isDead()) {
                    return player;
                }
            }
        }
        return null;
    }

    private void cyclePlayer() {
        HangmanPlayer player = getNextPlayer();
        state.setCurrentPlayer(player);
    }

    private boolean isGameOver() {
        boolean allPlayersDead = state.getPlayers().stream().allMatch(HangmanPlayer::isDead);
        boolean wordGuessed = state.getBoard().stream().noneMatch(c->c.equals(EMPTY_LETTER));
        return allPlayersDead || wordGuessed;
    }

    public Mono<?> start() {
        ui.setState(state);
        for (int i = 0; i < word.getValue().length(); i++) {
            state.getBoard().add(EMPTY_LETTER);
        }
        cyclePlayer();
        return run().then(Mono.defer(this::stop));
    }

    private Mono<?> run() {
        return ui.showBoard().then(ui.waitForGuesses()
            .takeUntil(c->isGameOver())
            .onErrorStop()
            .flatMap(g -> processGuess(g).then(Mono.defer(() -> {
                if (!isGameOver()) {
                    return ui.showBoard();
                }
                return Mono.error(new Exception("Game over."));
            })))
            .doOnNext(a -> {
                cyclePlayer();
                state.incrementTurn();
            })
            .then()
        );
    }

    private Mono<?> stop() {
        int betPool = state.getPlayers().stream().mapToInt(HangmanPlayer::getBet).sum();
        betPool *= 1.25;
        betPool += (15 * state.getPlayers().size());

        List<String> lines = new ArrayList<>();
        int i = 0;
        for(var player: state.getPlayers()) {
            String name = player.getUser().getMention();
            if (player.isDead()) {
                humanRepository.decreaseGold(player.getHumanId(), player.getBet());
                lines.add("%s\nLost %s **%s**".formatted(name, Emoji.EURO, player.getBet()));
                continue;
            }

            int percentageGuessed = player.getPercentageGuessed(word.getValue().length());
            int won = (int)Math.ceil(percentageGuessed / 100.0 * betPool);

            int nextWon = 0;
            if (state.getPlayers().size() > 1 && i == 0) {
                HangmanPlayer nextPlayer = state.getPlayers().get(1);
                nextWon = (int)Math.ceil(nextPlayer.getPercentageGuessed(word.getValue().length()) / 100.0 * betPool);
            }
            if (won > nextWon) {
                name += " 🌟";
            }

            int totalWorth = player.getGuesses().stream().mapToInt(Guess::getWorth).sum();

            humanRepository.increaseGold(player.getHumanId(), won);
            lines.add("%s\nWon %s **%s** (%s/%s) guessed".formatted(name, Emoji.EURO, won, totalWorth, word.getValue().length()));
            i++;
        }

        var embed = EmbedHelper.normal(String.join("\n", lines));
        embed.addField(word.getValue(), word.getDescription() == null ? "no definition" : word.getDescription(), false);
        embed.title("Game has ended.");
        return ui.showEndGame(embed.build());
    }

}
