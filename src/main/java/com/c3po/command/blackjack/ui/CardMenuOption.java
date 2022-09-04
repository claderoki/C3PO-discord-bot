package com.c3po.command.blackjack.ui;

import com.c3po.game.card.Card;
import com.c3po.game.card.Deck;
import com.c3po.ui.input.base.SelectMenuMenuOption;
import lombok.Setter;

import java.util.List;

public class CardMenuOption<C extends Card<?>> extends SelectMenuMenuOption<C> {
    private final Deck<C> deck;
    @Setter
    private Integer limit = null;

    public CardMenuOption(Deck<C> deck) {
        this("Draw", deck);
    }

    public CardMenuOption(String name, Deck<C> deck) {
        super(name);
        this.deck = deck;
    }

    @Override
    protected List<C> fetchOptions() {
        if (limit == null) {
            return deck.getCards();
        }
        return deck.getCards().stream().limit(limit).toList();
    }

    @Override
    protected String toValue(C value) {
        return value.getValue().toString();
    }

    @Override
    protected String toLabel(C value) {
        return value.getValue().toString();
    }
}
