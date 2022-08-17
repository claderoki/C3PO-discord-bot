package com.c3po.command.snakeoil.game;

import com.c3po.ui.input.base.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GameState {
    private final List<SnakeOilPlayer> players;
    private final List<Profession> professions;
    private final Deck deck;
    @Setter
    private Profession chosenProfession;
    @Setter
    private SnakeOilPlayer previousWinner;

    public void resetTurn() {
        players.forEach(c -> c.setTurnStatus(TurnStatus.WAITING_FOR_TURN));
        chosenProfession = null;
        previousWinner = null;
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
        resetTurn();
        SnakeOilPlayer old = players.stream().filter(SnakeOilPlayer::isKing).findFirst().orElse(null);
        SnakeOilPlayer player = getNextPlayer(old);
        if (old != null) {
            old.setKing(false);
        }
        player.setKing(true);
        player.setTurnStatus(TurnStatus.PICKING);
        menu.setEmbedConsumer(e -> ui.getEmbed(this, e));
        return Mono.empty();
    }

    public void nextPicking() {
        SnakeOilPlayer previous = players.stream()
            .filter(c -> c.getTurnStatus().equals(TurnStatus.PICKING))
            .findFirst()
            .orElseThrow();

        boolean cardPickersFinished = players.stream().filter(c -> !c.isKing()).allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));
        if (cardPickersFinished && previousWinner == null) {
            players.stream().filter(SnakeOilPlayer::isKing).forEach(c -> c.setTurnStatus(TurnStatus.PICKING));
        } else {
            SnakeOilPlayer player = getNextPlayer(previous);
            player.setTurnStatus(TurnStatus.PICKING);
        }

        previous.setTurnStatus(TurnStatus.FINISHED);
    }
}
