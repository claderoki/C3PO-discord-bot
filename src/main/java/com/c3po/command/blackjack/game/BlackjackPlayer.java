package com.c3po.command.blackjack.game;

import com.c3po.game.card.Deck;
import com.c3po.game.card.PlayingCard;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlackjackPlayer {
    private final User user;
    private final Deck<PlayingCard> cards;
}
