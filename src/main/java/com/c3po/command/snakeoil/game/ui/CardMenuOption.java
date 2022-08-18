package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.card.Card;
import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.card.Word;
import discord4j.core.object.component.SelectMenu;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardMenuOption extends SnakeOilMenuOption {
    public CardMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Select your cards", gameState, player);
    }

    @Override
    public SelectMenu getComponent() {
        return SelectMenu.of(getCustomId(), getOptions()).withMaxValues(2).withMinValues(2);
    }

    protected Map<String, String> getOptionCache() {
        return player.getDeck().getCards().stream().collect(Collectors.toMap(Card::getValue, Card::getValue));
    }

    @Override
    protected void afterHook() {
        List<Word> words = getValue().stream().map(w -> player.getDeck().find(c -> c.getValue().equals(w))).toList();
        for(Word word: words) {
            player.getDeck().removeCard(word);
            gameState.getCurrentRound().addWord(player, word);
        }
    }
}
