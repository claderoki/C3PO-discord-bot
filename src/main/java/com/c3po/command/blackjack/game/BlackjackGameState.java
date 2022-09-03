package com.c3po.command.blackjack.game;

import com.c3po.game.card.Deck;
import com.c3po.game.card.PlayingCard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class BlackjackGameState {
    private final Deck<PlayingCard> deck;
    private final List<BlackjackPlayer> players;
}
