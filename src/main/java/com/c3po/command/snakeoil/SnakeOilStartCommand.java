package com.c3po.command.snakeoil;

import com.c3po.command.snakeoil.game.*;
import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.connection.repository.WordRepository;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.wordnik.WordnikApi;
import com.c3po.core.wordnik.endpoints.GetRandomWords;
import com.c3po.core.wordnik.endpoints.GetWordDefinition;
import com.c3po.core.wordnik.responses.WordResponse;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeOilStartCommand extends SubCommand {

    protected SnakeOilStartCommand(SnakeOilCommandGroup group) {
        super(group, "start", "Start the game.");
    }

    private SnakeOilPlayer toPlayer(User user) {
        return new SnakeOilPlayer(user, new Deck<>());
    }

    private Mono<LinkedHashSet<User>> getUsers(Context context) {
        return Mono.just(Stream.of(120566758091259906L, 286986959115517952L, 150026023931609088L)
            .map(u -> context.getEvent().getClient().getUserById(Snowflake.of(u)).block())
            .collect(Collectors.toList())
        ).map(LinkedHashSet::new);

//        LobbyMenu menu = new LitForMenu(menu).then(Mono.just(new LinkedHashSet<>(menu.getUsers())));
    }
    private Flux<Word> getApiWords() {
        WordnikApi api = new WordnikApi();
        GetRandomWords endpoint = new GetRandomWords();
        endpoint.setLimit(30);
        return api.call(endpoint)
            .flatMapIterable(w -> w.getWords().stream().map(WordResponse::getWord).toList())
            .map(c -> api.call(new GetWordDefinition(c)))
            .flatMap(d -> d)
            .map(d -> new Word(d.getWord(), d.getDefinitions().get(0).getText()))
            ;
    }
    private final WordRepository wordRepository = WordRepository.db();

    private Mono<Void> cacheWords() {
        return getApiWords()
            .map(word -> {
                wordRepository.createWord(word.getValue(), word.getDescription());
                return word;
            })
            .then();
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
        return cacheWords();

//        return getUsers(context)
//            .map(users -> users.stream().map(this::toPlayer).toList())
//            .filter(users -> !users.isEmpty())
//            .map(players -> {
//                Deck<Profession> professions = new Deck<>(getProfessions().stream().map(Profession::new).toList());
//                Deck<Word> deck = new Deck<>(getWords().stream().map(w -> new Word(w, null)).toList());
//                GameState gameState = new GameState(players, professions, deck);
//                return new SnakeOilGame(gameState, new SnakeOilUI(context));
//            })
//            .flatMap(SnakeOilGame::start)
//            .then();
    }
}