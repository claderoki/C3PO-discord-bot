package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;

import java.util.Map;
import java.util.stream.Collectors;

public class ProfessionMenuOption extends SnakeOilMenuOption {
    public ProfessionMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Select your profession", gameState, player);
    }

    protected Map<String, String> getOptionCache() {
        return gameState.getProfessions().getCards().stream().limit(2).collect(Collectors.toMap(Profession::getValue, Profession::getValue));
    }

    @Override
    protected void afterHook() {
        Profession profession = gameState.getProfessions().getCards().stream().filter(c -> c.getValue().equals(getValue().get(0)))
            .findFirst()
            .orElseThrow();
        gameState.getCurrentRound().setProfession(profession);
    }

}