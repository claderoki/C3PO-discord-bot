package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.card.Word;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.List;

public class CardMenuOption extends SnakeOilMenuOption<Word> {
    public CardMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Select your product", gameState, player);
    }

    @Override
    protected SelectMenu modifySelectMenu(SelectMenu selectMenu) {
        return selectMenu.withMaxValues(2).withMinValues(2);
    }

    @Override
    protected List<Word> fetchOptions() {
        return player.getWords().getCards();
    }

    @Override
    protected String toValue(Word value) {
        return value.getValue();
    }

    @Override
    protected String toLabel(Word value) {
        return value.getValue();
    }

    @Override
    protected void afterHook() {
        List<Word> words = getSelected();
        for(Word word: words) {
            player.getWords().removeCard(word);
            gameState.getCurrentRound().addWord(player, word);
        }
        player.getWords().drawFrom(gameState.getWords(), 2);
    }

    @Override
    protected String getFollowupDescription() {
        String format = "Last turn, %s tried to sell **%s** to %s (%s)";
        return format.formatted(
            player,
            gameState.getCurrentRound().getProduct(player),
            gameState.getCurrentRound().getCustomer(),
            gameState.getCurrentRound().getProfession().getValue()
        );
    }

}
