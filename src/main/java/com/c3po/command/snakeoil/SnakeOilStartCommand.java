package com.c3po.command.snakeoil;

import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.command.snakeoil.game.*;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeOilStartCommand extends SubCommand {
    protected SnakeOilStartCommand(SnakeOilCommandGroup group) {
        super(group, "start", "Start the game.");
    }

    private SnakeOilPlayer toPlayer(User user) {
        Deck deck = new Deck();
        return new SnakeOilPlayer(user, deck);
    }

    private Mono<LinkedHashSet<User>> getUsers(Context context) {
        return Mono.just(Stream.of(120566758091259906L, 150026023931609088L)
            .map(u -> context.getEvent().getClient().getUserById(Snowflake.of(u)).block())
            .collect(Collectors.toList())
        ).map(LinkedHashSet::new);

//        LobbyMenu menu = new LobbyMenu(context);
//        return MenuManager.waitForMenu(menu).then(Mono.just(menu.getUsers()));
    }

    public Set<String> getWords() {
        return Set.of(
            "horse",
            "tomato",
            "dog",
            "poop",
            "cat",
            "time",
            "clock",
            "keyboard",
            "piano",
            "button",
            "sauce",
            "phone",
            "bottle",
            "window",
            "plush",
            "plant",
            "toilet",
            "water"
        );
    }

    public Set<String> getProfessions() {
        return Set.of(
            "lawyer",
            "cook",
            "doctor",
            "programmer",
            "race car driver",
            "priest",
            "teacher"
        );
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        return getUsers(context)
            .map(users -> users.stream().map(this::toPlayer).toList())
            .filter(users -> !users.isEmpty())
            .map(players -> {
                List<Profession> professions = getProfessions().stream().map(Profession::new).toList();
                Deck deck = new Deck(getWords().stream().map(Card::new).toList());
                GameState gameState = new GameState(players, professions, deck);
                return new SnakeOilGame(gameState, new SnakeOilUI(context));
            })
            .flatMap(SnakeOilGame::start)
            .then();
    }
}
