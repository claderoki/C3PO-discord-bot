package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.command.snakeoil.game.TurnStatus;
import com.c3po.command.snakeoil.game.card.Card;
import discord4j.core.object.entity.User;

import java.util.Map;
import java.util.stream.Collectors;

public class PersonMenuOption extends SnakeOilMenuOption {
    private SnakeOilPlayer winner;
    public PersonMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Choose the product you'd like to buy", gameState, player);
    }

    protected Map<String, String> getOptionCache() {
        return gameState.getPlayers().stream()
            .filter(c -> !c.equals(player))
            .collect(Collectors.toMap(p -> p.getUser().getId().asString(), p -> gameState.getCurrentRound().getProduct(p)));
    }

    @Override
    protected void afterHook() {
        winner = gameState.getPlayers().stream()
            .filter(c -> getValue().contains(c.getUser().getId().asString()))
            .findFirst()
            .orElseThrow();
        winner.incrementScore();
        gameState.getCurrentRound().setWinner(winner);
    }

    @Override
    protected String getFollowupDescription() {
        return "Last turn, " + player + " bought **" + gameState.getCurrentRound().getProduct(winner) + "** from " + winner;
    }

}
