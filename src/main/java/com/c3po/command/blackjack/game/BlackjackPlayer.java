package com.c3po.command.blackjack.game;

import com.c3po.game.card.Deck;
import com.c3po.game.card.PlayingCard;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class BlackjackPlayer {
    private final User user;
    private final Deck<PlayingCard> cards;
    @Setter
    private boolean finished;
    @Setter
    private PlayerStatus status = PlayerStatus.UNDECIDED;
}
