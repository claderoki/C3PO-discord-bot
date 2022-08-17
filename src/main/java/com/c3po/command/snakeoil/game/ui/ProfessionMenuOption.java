package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.Profession;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.TurnStatus;

import java.util.Map;
import java.util.stream.Collectors;

public class ProfessionMenuOption extends SnakeOilMenuOption {
    public ProfessionMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Select your profession", gameState, player);
    }

    protected Map<String, String> getOptionCache() {
        return gameState.getProfessions().stream().limit(2).collect(Collectors.toMap(Profession::name, Profession::name));
    }

    @Override
    protected void afterHook() {
        Profession profession = gameState.getProfessions().stream().filter(c -> c.name().equals(getValue().get(0)))
            .findFirst()
            .orElseThrow();
        gameState.setChosenProfession(profession);
    }

}
