package com.c3po.command.snakeoil.game;

import com.c3po.ui.input.base.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class GameState {
    private final List<SnakeOilPlayer> players;
    private final List<Profession> professions;
    private final Deck deck;
    private SnakeOilPlayer turn;
    private Profession chosenProfession;
    private final HashMap<SnakeOilPlayer, TurnStatus> statuses = new HashMap<>();
    private SnakeOilPlayer previousWinner;

    public void resetTurn() {
        statuses.clear();
        statuses.putAll(players.stream().collect(Collectors.toMap(c->c, c -> TurnStatus.PICKING)));
        chosenProfession = null;
        turn = null;
        previousWinner = null;
    }

    public void addStatus(SnakeOilPlayer player, TurnStatus status) {
        statuses.put(player, status);
    }

    public void nextPlayer() {
        if (turn == null) {
            turn = players.get(0);
        } else {
            int currentIndex = players.indexOf(turn);
            if (currentIndex == players.size()) {
                turn = players.get(0);
            } else {
                turn = players.get(currentIndex+1);
            }
        }
    }

    public Mono<?> newTurn(Menu menu, SnakeOilUI ui) {
        resetTurn();
        nextPlayer();
        menu.setEmbedConsumer(e -> ui.getEmbed(this, e));
        return Mono.empty();
    }

}
