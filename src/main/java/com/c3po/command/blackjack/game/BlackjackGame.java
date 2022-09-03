package com.c3po.command.blackjack.game;

import com.c3po.command.blackjack.menu.BlackjackMenuOption;
import com.c3po.command.blackjack.menu.CardMenuOption;
import com.c3po.game.card.PlayingCard;
import com.c3po.ui.input.SubMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.SubMenu;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BlackjackGame {
    private final BlackjackGameState gameState;
    private final BlackjackUI ui;

    public Mono<Void> start() {
        gameState.getDeck().shuffle();
        Menu menu = new Menu(ui.getContext());
        menu.setEmbedConsumer(e -> e.description("BALCKJACK THE GAME"));
        for (BlackjackPlayer player: gameState.getPlayers()) {
            player.getCards().drawFrom(gameState.getDeck(), 1);
            SubMenu subMenu = new SubMenu(ui.getContext(), new BlackjackMenuOption(player));
            SubMenuOption option = new SubMenuOption(player.getUser().getUsername(), subMenu);
            menu.addOption(option);
        }
        return new MenuManager<>(menu).waitFor().then();
    }
}