package com.c3po.command.snakeoil.game;

import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.ui.input.base.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class GameState {
    private final List<SnakeOilPlayer> players;
    private final Deck<Profession> professions;
    private final Deck<Word> words;
    private final ArrayList<RoundState> rounds = new ArrayList<>();
    private RoundState currentRound;

    public void resetTurn() {
        players.forEach(c -> c.setTurnStatus(TurnStatus.WAITING_FOR_TURN));
        currentRound = new RoundState();
        rounds.add(currentRound);
    }

    public SnakeOilPlayer getNextPlayer(SnakeOilPlayer current) {
        if (current == null) {
            return players.get(0);
        } else {
            int currentIndex = players.indexOf(current);
            if (currentIndex == players.size()-1) {
                return players.get(0);
            } else {
                return players.get(currentIndex+1);
            }
        }
    }

    public Mono<?> newTurn(Menu menu, SnakeOilUI ui) {
        RoundState previousRound = currentRound;
        resetTurn();
        SnakeOilPlayer previousKing = previousRound != null ? previousRound.getKing() : null;
        SnakeOilPlayer player = getNextPlayer(previousKing);
        player.setTurnStatus(TurnStatus.PICKING);
        menu.setEmbedConsumer(e -> ui.getEmbed(this, e));
        currentRound.setKing(player);
        return Mono.empty();
    }

    public void nextPicking() {
        SnakeOilPlayer previous = players.stream()
            .filter(c -> c.getTurnStatus().equals(TurnStatus.PICKING))
            .findFirst()
            .orElseThrow();

        boolean cardPickersFinished = players.stream().filter(c -> c != currentRound.getKing()).allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));
        if (cardPickersFinished && currentRound.getWinner() == null) {
            currentRound.getKing().setTurnStatus(TurnStatus.PICKING);
        } else {
            SnakeOilPlayer player = getNextPlayer(previous);
            player.setTurnStatus(TurnStatus.PICKING);
        }
        previous.setTurnStatus(TurnStatus.FINISHED);
    }
}
