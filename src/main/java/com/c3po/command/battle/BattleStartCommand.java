package com.c3po.command.battle;

import com.c3po.command.battle.entity.BattlePlayer;
import com.c3po.command.battle.entity.monster.Slime;
import com.c3po.command.battle.game.BattleGame;
import com.c3po.command.battle.game.BattleGameState;
import com.c3po.command.battle.game.BattleUI;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BattleStartCommand extends SubCommand {
    protected BattleStartCommand() {
        super(CommandCategory.BATTLE, "start", "Start the game.");
    }

    private BattlePlayer toPlayer(User user) {
        return new BattlePlayer(user);
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
            .map(players -> new BattleGameState(players, List.of(new Slime(), new Slime())))
            .map(state -> new BattleGame(state, new BattleUI(context)))
            .flatMap(BattleGame::start)
            .then();
    }
}
