package com.c3po.command.snakeoil.game;

import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.ui.input.base.Menu;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class GameState {
    private final List<SnakeOilPlayer> players;
    private final Deck<Profession> professions;
    private final Deck<Word> words;
    private final boolean test;
    private final ArrayList<RoundState> rounds = new ArrayList<>();
    private RoundState currentRound;
    @Setter
    private Message previousNotification;
    private SnakeOilPlayer currentlyPicking;

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

    public Mono<Void> newTurn(Menu menu, SnakeOilUI ui) {
        RoundState previousRound = currentRound;
        resetTurn();
        SnakeOilPlayer previousCustomer = previousRound != null ? previousRound.getCustomer() : null;
        currentlyPicking = getNextPlayer(previousCustomer);
        currentlyPicking.setTurnStatus(TurnStatus.PICKING);
        menu.setEmbedConsumer(e -> ui.getEmbed(this, e));
        currentRound.setCustomer(currentlyPicking);
        players.forEach(c -> c.setStatus(getStatus(c)));
        return Mono.empty();
    }

    public void nextPicking() {
        SnakeOilPlayer previous = currentlyPicking;
        SnakeOilPlayer player;
        boolean cardPickersFinished = players.stream().filter(c -> c != currentRound.getCustomer()).allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));
        if (cardPickersFinished && currentRound.getWinner() == null) {
            player = currentRound.getCustomer();
        } else {
            player = getNextPlayer(previous);
        }
        player.setTurnStatus(TurnStatus.PICKING);
        previous.setTurnStatus(TurnStatus.FINISHED);
        players.forEach(c -> c.setStatus(getStatus(c)));
        currentlyPicking = player;
    }

    public PlayerStatus getStatus(SnakeOilPlayer player) {
        boolean allWordsChosen = getPlayers()
            .stream()
            .filter(c -> c != getCurrentRound().getCustomer())
            .allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));

        if (getCurrentRound().getCustomer().equals(player)) {
            if (allWordsChosen) {
                return PlayerStatus.PICKING_PERSON;
            } else {
                return PlayerStatus.PICKING_PROFESSION;
            }
        } else {
            return PlayerStatus.PICKING_CARD;
        }
    }
}
