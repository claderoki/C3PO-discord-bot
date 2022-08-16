package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.Card;
import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.TurnStatus;
import discord4j.core.object.component.SelectMenu;

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
        return gameState.getTurn().deck().getCards().stream().collect(Collectors.toMap(Card::getWord, Card::getWord));
    }

    @Override
    protected void afterHook() {
        List<Card> cards = gameState.getTurn().deck().getCards().stream().filter(c -> getValue().contains(c.getWord())).toList();
        for(Card card: cards) {
            card.setSelected(true);
        }
        gameState.addStatus(player, TurnStatus.FINISHED);
    }
}
