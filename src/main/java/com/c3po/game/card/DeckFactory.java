package com.c3po.game.card;

import com.c3po.game.card.standard.Rank;
import com.c3po.game.card.standard.Suit;

public class DeckFactory {
    private static Deck<PlayingCard> createStandard52(int amount) {
        Deck<PlayingCard> deck = new Deck<>();
        for (int i = 0; i < amount; i++) {
            for(Suit suit: Suit.values()) {
                for(Rank rank: Rank.values()) {
                    deck.addCard(new PlayingCard(rank, suit));
                }
            }
        }
        return deck;
    }

    public static <T extends Card<?>> Deck<T> create(DeckType type) {
        return create(type, 1);
    }

    public static <T extends Card<?>> Deck<T> create(DeckType type, int amount) {
        return switch (type) {
            case STANDARD_52 -> (Deck<T>) createStandard52(amount);
        };
    }
}
