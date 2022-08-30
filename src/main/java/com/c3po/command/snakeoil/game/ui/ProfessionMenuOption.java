package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import reactor.core.publisher.Mono;

import java.util.List;

public class ProfessionMenuOption extends SnakeOilMenuOption<Profession> {
    private static final int AMOUNT = 2;

    public ProfessionMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Who are you?", gameState, player);
    }

    @Override
    protected void afterHook() {
        Profession profession = getSelected().stream()
            .findFirst()
            .orElseThrow();

        gameState.getCurrentRound().setProfession(profession);
        for(int i = 0; i < AMOUNT; i++) {
            gameState.getProfessions().take();
        }
    }

    @Override
    protected String getFollowupDescription() {
        return "" + player.getUser().getUsername() + " is a **" + gameState.getCurrentRound().getProfession().getValue() + "**";
    }

    @Override
    protected List<Profession> fetchOptions() {
        return gameState.getProfessions().getCards().stream().limit(AMOUNT).toList();
    }

    @Override
    protected String toValue(Profession value) {
        return value.getValue();
    }

    @Override
    protected String toLabel(Profession value) {
        return value.getValue();
    }
}
