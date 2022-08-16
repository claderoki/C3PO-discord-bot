package com.c3po.command.snakeoil.game;

import com.c3po.command.hangman.game.core.Game;
import com.c3po.command.snakeoil.game.ui.SnakeOilButton;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SnakeOilGame extends Game {
    private final GameState gameState;
    private final SnakeOilUI ui;

    public Mono<?> start() {
        Menu menu = new Menu(ui.getContext());
        menu.setOwnerOnly(false);
        menu.setEmbedConsumer(e -> ui.getEmbed(gameState, e));
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            player.deck().drawFrom(gameState.getDeck(), 2);
            SnakeOilButton button = new SnakeOilButton(gameState, player, ui);
            menu.addOption(button);
        }
        gameState.newTurn(menu, ui);
        return MenuManager.waitForMenu(menu);
    }

}
