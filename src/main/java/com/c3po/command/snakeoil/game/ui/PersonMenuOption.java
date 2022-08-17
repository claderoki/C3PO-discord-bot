package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.TurnStatus;
import discord4j.core.object.entity.User;

import java.util.Map;
import java.util.stream.Collectors;

public class PersonMenuOption extends SnakeOilMenuOption {
    public PersonMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Select the winner", gameState, player);
    }

    protected Map<String, String> getOptionCache() {
        return gameState.getPlayers().stream()
            .filter(c -> !c.equals(player))
            .map(SnakeOilPlayer::getUser)
            .collect(Collectors.toMap(u -> u.getId().asString(), User::getUsername));
    }

    @Override
    protected void afterHook() {
        SnakeOilPlayer player = gameState.getPlayers().stream()
            .filter(c -> getValue().contains(c.getUser().getId().asString()))
            .findFirst()
            .orElseThrow();
        player.incrementScore();
        gameState.setPreviousWinner(player);
    }
}
