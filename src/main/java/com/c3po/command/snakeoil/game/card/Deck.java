package com.c3po.command.snakeoil.game.card;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@NoArgsConstructor
@Getter
public final class Deck<T extends Card<?>> {
    private final ArrayList<T> cards = new ArrayList<>();

    public Deck(Collection<T> cards) {
        this.cards.addAll(cards);
    }

    public T find(Function<T, Boolean> matcher) {
        for(T card: cards) {
            if (matcher.apply(card)) {
                return card;
            }
        }
        return null;
    }

    public void addCard(T card) {
        this.cards.add(card);
    }

    public void removeCard(T card) {
        this.cards.remove(card);
    }

    public void shuffle() {}

    public void drawFrom(Deck<T> deck, int amount) {
        for(int i = 0; i < amount; i++) {
            addCard(deck.take());
        }
    }

    public T take() {
        T card = cards.get(0);
        cards.remove(card);
        return card;
    }

}
