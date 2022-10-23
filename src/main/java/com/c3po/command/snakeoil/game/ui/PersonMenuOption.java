package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;

import java.util.List;

public class PersonMenuOption extends SnakeOilMenuOption<SnakeOilPlayer> {
    private SnakeOilPlayer winner;
    public PersonMenuOption(GameState gameState, SnakeOilPlayer player) {
        super("Choose the product you'd like to buy", gameState, player);
    }

    @Override
    protected void afterHook() {
        winner = getValue().stream().findFirst().orElseThrow();
        winner.incrementScore();
        gameState.getCurrentRound().setWinner(winner);
    }

    @Override
    protected String getFollowupDescription() {
        return "Last turn, " + player + " bought **" + gameState.getCurrentRound().getProduct(winner) + "** from " + winner;
    }

    @Override
    protected List<SnakeOilPlayer> fetchOptions() {
        return gameState.getPlayers().stream().filter(c -> !c.equals(player)).toList();
    }

    @Override
    protected String toValue(SnakeOilPlayer value) {
        return value.getUser().getId().asString();
    }

    @Override
    protected String toLabel(SnakeOilPlayer value) {
        return gameState.getCurrentRound().getProduct(value);
    }
}
