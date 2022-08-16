package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.ui.input.base.SelectMenuMenuOption;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class SnakeOilMenuOption extends SelectMenuMenuOption {
    protected final GameState gameState;
    protected final SnakeOilPlayer player;

    @Override
    protected boolean shouldContinue() {
        return false;
    }

    public SnakeOilMenuOption(String name, GameState gameState, SnakeOilPlayer player) {
        super(name);
        this.gameState = gameState;
        this.player = player;
    }

    protected abstract void afterHook();

    @Override
    final public Mono<?> execute(SelectMenuInteractionEvent event) {
        return super.execute(event)
            .then(Mono.defer(() -> {
                afterHook();
                return Mono.empty();
            }))
            .then(event.deferEdit());
    }
}
