package com.c3po.game.card;

import com.c3po.game.card.standard.Rank;
import com.c3po.game.card.standard.Suit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayingCard extends Card<Rank> {
    private final Suit suit;
    private boolean hidden = false;

    public PlayingCard(Rank rank, Suit suit) {
        super(rank);
        this.suit = suit;
    }
}
