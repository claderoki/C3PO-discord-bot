package com.c3po.command.snakeoil;

import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.command.snakeoil.game.*;
import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import org.apache.maven.model.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeOilStartCommand extends SubCommand {

    protected SnakeOilStartCommand(SnakeOilCommandGroup group) {
        super(group, "start", "Start the game.");
        addOption(o -> o
            .name("test")
            .description("Test")
            .type(DiscordCommandOptionType.BOOLEAN.getValue())
            .required(false)
        );
    }

    private SnakeOilPlayer toPlayer(User user) {
        return new SnakeOilPlayer(user, new Deck<>());
    }

    private Mono<LinkedHashSet<User>> getUsers(Context context, boolean test) {
        if (test) {
            return Flux.fromStream(Stream.of(120566758091259906L, 286986959115517952L, 150026023931609088L))
                .map(u -> context.getEvent().getClient().getUserById(Snowflake.of(u)))
                .flatMap(u -> u)
                .collect(Collectors.toSet())
                .map(LinkedHashSet::new);
        }
        return MenuManager.waitForMenu(new LobbyMenu(context)).map(m -> new LinkedHashSet<>(((LobbyMenu)m).getUsers()));
    }

    private Set<String> readLines(String csvFile) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("snakeoil/" + csvFile);
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().collect(Collectors.toSet());
    }

    public Set<String> getWords() {
        return readLines("Words.csv");
    }

    public Set<String> getProfessions() {
        return readLines("Professions.csv");
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        Boolean test = context.getOptions().optBool("test");
        if (test == null) {
            test = false;
        }

        return getUsers(context, test)
            .map(users -> users.stream().map(this::toPlayer).toList())
            .filter(users -> !users.isEmpty())
            .map(players -> {
                Deck<Profession> professions = new Deck<>(getProfessions().stream().map(Profession::new).toList());
                Deck<Word> deck = new Deck<>(getWords().stream().map(w -> new Word(w, null)).toList());
                GameState gameState = new GameState(players, professions, deck);
                return new SnakeOilGame(gameState, new SnakeOilUI(context));
            })
            .flatMap(SnakeOilGame::start)
            .then();
    }
}
