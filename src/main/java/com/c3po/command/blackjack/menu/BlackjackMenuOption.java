package com.c3po.command.blackjack.menu;

import com.c3po.command.blackjack.game.BlackjackPlayer;
import com.c3po.game.card.PlayingCard;

public class BlackjackMenuOption extends CardMenuOption<PlayingCard> {
    private final BlackjackPlayer player;

    public BlackjackMenuOption(BlackjackPlayer player) {
        super(player.getCards());
        this.player = player;
    }
}
