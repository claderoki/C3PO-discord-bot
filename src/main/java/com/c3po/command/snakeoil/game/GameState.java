package com.c3po.command.snakeoil.game;

import com.c3po.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.helper.Cycler;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameState {
    private final List<SnakeOilPlayer> players;
    private final Deck<Profession> professions;
    private final Deck<Word> words;
    private final boolean test;
    private final Cycler<SnakeOilPlayer> cycler;
    private final ArrayList<RoundState> rounds = new ArrayList<>();
    private RoundState currentRound;
    @Setter
    private Message previousNotification;
    private SnakeOilPlayer currentlyPicking;

    public GameState(List<SnakeOilPlayer> players, Deck<Profession> professions, Deck<Word> words, boolean test) {
        this.players = players;
        this.professions = professions;
        this.words = words;
        this.test = test;
        this.cycler = new Cycler<>(players);
    }

    private void resetTurn() {
        players.forEach(c -> c.setTurnStatus(TurnStatus.WAITING_FOR_TURN));
        int currentPlayerIndex = currentlyPicking == null ? 0 : players.indexOf(currentlyPicking);
        currentRound = new RoundState(new Cycler<>(players, currentPlayerIndex));
        rounds.add(currentRound);
    }

    public void newTurn() {
        resetTurn();
        currentlyPicking = cycler.next();
        currentlyPicking.setTurnStatus(TurnStatus.PICKING);
        currentRound.setCustomer(currentlyPicking);
        players.forEach(c -> c.setStatus(getStatus(c)));
    }

    public void nextPicking() {
        currentlyPicking.setTurnStatus(TurnStatus.FINISHED);
        currentlyPicking = currentRound.getCycler().next();
        currentlyPicking.setTurnStatus(TurnStatus.PICKING);
        players.forEach(c -> c.setStatus(getStatus(c)));
    }

    public PlayerStatus getStatus(SnakeOilPlayer player) {
        boolean allWordsChosen = getPlayers()
            .stream()
            .filter(c -> c != getCurrentRound().getCustomer())
            .allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));

        if (currentRound.getCustomer().equals(player)) {
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
