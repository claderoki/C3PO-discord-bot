package com.c3po.game.card;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor
@Getter
public final class Deck<T extends Card<?>> {
    private final ArrayList<T> cards = new ArrayList<>();

    public Deck(Collection<T> cards) {
        this.cards.addAll(cards);
    }

    public Optional<T> find(Function<T, Boolean> matcher) {
        for(T card: cards) {
            if (matcher.apply(card)) {
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    public void addCard(T card) {
        this.cards.add(card);
    }

    public void removeCard(T card) {
        this.cards.remove(card);
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

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
