package com.c3po.command.snakeoil;

import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.command.snakeoil.game.*;
import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.connection.repository.ReminderRepository;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.resource.Resource;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeOilStartCommand extends SubCommand {

    @Autowired
    protected ReminderRepository reminderRepository;

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

    private Stream<Long> getTestUserIds(Context context) {
        long guildId = context.getEvent().getInteraction().getGuildId().orElseThrow().asLong();
        if (guildId == 729843647347949638L) {
            return Stream.of(120566758091259906L, 247855177074212865L, 321028231299858432L);
        } else {
            return Stream.of(120566758091259906L, 286986959115517952L, 150026023931609088L);
        }
    }

    private Mono<LinkedHashSet<User>> getUsers(Context context, boolean test) {
        if (test) {
            return Flux.fromStream(getTestUserIds(context))
                .map(u -> context.getEvent().getClient().getUserById(Snowflake.of(u)))
                .flatMap(u -> u)
                .collect(Collectors.toSet())
                .map(LinkedHashSet::new);
        }
        return new MenuManager(new LobbyMenu(context)).waitFor().map(m -> new LinkedHashSet<>(((LobbyMenu)m).getUsers()));
    }

    private Set<String> readLines(String csvFile) {
        Resource resource = new Resource("snakeoil/" + csvFile);
        return resource.getLines().collect(Collectors.toSet());
    }

    public Set<String> getWords() {
        return readLines("Words.csv");
    }

    public Set<String> getProfessions() {
        return readLines("Professions.csv");
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Boolean test = context.getOptions().optBool("test");
        if (test == null) {
            test = false;
        }

        Boolean finalTest = test;
        return getUsers(context, test)
            .map(users -> users.stream().map(this::toPlayer).toList())
            .filter(users -> !users.isEmpty())
            .map(players -> {
                Deck<Profession> professions = new Deck<>(getProfessions().stream().map(Profession::new).toList());
                Deck<Word> deck = new Deck<>(getWords().stream().map(Word::new).toList());
                GameState gameState = new GameState(players, professions, deck, finalTest);
                return new SnakeOilGame(gameState, new SnakeOilUI(context));
            })
            .flatMap(SnakeOilGame::start)
            .then();
    }
}
