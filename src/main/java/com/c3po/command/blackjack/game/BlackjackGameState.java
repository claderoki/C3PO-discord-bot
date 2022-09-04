package com.c3po.command.blackjack.game;

import com.c3po.game.card.Deck;
import com.c3po.game.card.PlayingCard;
import com.c3po.helper.Cycler;
import lombok.Getter;

import java.util.List;

@Getter
public class BlackjackGameState {
    private final Deck<PlayingCard> deck;
    private final List<BlackjackPlayer> players;
    private final Cycler<BlackjackPlayer> cycler;
    private BlackjackPlayer current;

    public BlackjackGameState(Deck<PlayingCard> deck, List<BlackjackPlayer> players) {
        this.deck = deck;
        this.players = players;
        this.cycler = new Cycler<>(players);
    }

    public void nextTurn() {
        current = cycler.next();
    }
}
