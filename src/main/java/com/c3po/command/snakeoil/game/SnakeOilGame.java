package com.c3po.command.snakeoil.game;

import com.c3po.command.hangman.game.core.Game;
import com.c3po.command.snakeoil.game.ui.ShowDeckButton;
import com.c3po.command.snakeoil.game.ui.ShowInstructionsButton;
import com.c3po.command.snakeoil.game.ui.SnakeOilButton;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SnakeOilGame extends Game {
    private final GameState gameState;
    private final SnakeOilUI ui;

    public Mono<Void> start() {
        gameState.getWords().shuffle();
        gameState.getProfessions().shuffle();
        Menu menu = new Menu(ui.getContext(), true);
        menu.setEmbedConsumer(e -> ui.getEmbed(gameState, e));
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            player.getWords().drawFrom(gameState.getWords(), 6);
            SnakeOilButton button = new SnakeOilButton(gameState, player);
            menu.addOption(button);
        }
        menu.addOption(new ShowDeckButton(gameState));
        menu.addOption(new ShowInstructionsButton());
        gameState.newTurn();
        return new MenuManager<>(menu).waitFor().then();
    }
}
