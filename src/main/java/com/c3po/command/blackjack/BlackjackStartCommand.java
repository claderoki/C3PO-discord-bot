package com.c3po.command.blackjack;

import com.c3po.command.blackjack.game.BlackjackGame;
import com.c3po.command.blackjack.game.BlackjackGameState;
import com.c3po.command.blackjack.game.BlackjackPlayer;
import com.c3po.command.blackjack.game.BlackjackUI;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.game.card.Deck;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.game.card.DeckFactory;
import com.c3po.game.card.DeckType;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BlackjackStartCommand extends SubCommand {
    protected BlackjackStartCommand() {
        super(CommandCategory.BLACKJACK, "start", "Start the game.");
    }

    private BlackjackPlayer toPlayer(User user) {
        return new BlackjackPlayer(user, new Deck<>());
    }

    private Flux<User> getUsers(Context context) {
        return new MenuManager<>(new LobbyMenu(context)).waitFor()
            .flux()
            .flatMap(m -> Flux.fromIterable(m.getUsers()));
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        return getUsers(context)
            .map(this::toPlayer)
            .collectList()
            .map(p -> new BlackjackGameState(DeckFactory.create(DeckType.STANDARD_52), p))
            .map(s -> new BlackjackGame(s, new BlackjackUI(context)))
            .flatMap(BlackjackGame::start)
            .then();
    }
}
