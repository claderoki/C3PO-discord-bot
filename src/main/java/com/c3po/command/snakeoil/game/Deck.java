package com.c3po.command.snakeoil.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@Getter
public final class Deck {
    private final ArrayList<Card> cards = new ArrayList<>();

    public Deck(Collection<Card> cards) {
        this.cards.addAll(cards);
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public void shuffle() {}

    public void drawFrom(Deck deck, int amount) {
        for(int i = 0; i < amount; i++) {
            addCard(deck.take());
        }
    }

    public Card take() {
        Card card = cards.get(0);
        cards.remove(card);
        return card;
    }

}
