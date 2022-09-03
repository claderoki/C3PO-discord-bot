package com.c3po.command.blackjack.game;

import com.c3po.command.blackjack.menu.BlackjackMenuOption;
import com.c3po.ui.input.SubMenuOption;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.SubMenu;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BlackjackGame {
    private final BlackjackGameState gameState;
    private final BlackjackUI ui;

    private SubMenu getPlayerSubmenu(BlackjackPlayer player) {
        SubMenu subMenu = new SubMenu(ui.getContext(), new VoidMenuOption("Hit"));
        subMenu.addOption(new VoidMenuOption("Draw"));
        return subMenu;
    }

    public Mono<Void> start() {
        gameState.getDeck().shuffle();
        Menu menu = new Menu(ui.getContext());
        menu.setEmbedConsumer(e -> e.description("BALCKJACK THE GAME"));
        for (BlackjackPlayer player: gameState.getPlayers()) {
            player.getCards().drawFrom(gameState.getDeck(), 1);
            SubMenuOption option = new SubMenuOption(player.getUser().getUsername(), getPlayerSubmenu(player), true);
            option.setEphemeral(true);
            menu.addOption(option);
        }
        return new MenuManager<>(menu).waitFor().then();
    }
}