package com.c3po.command.snakeoil;

import com.c3po.command.hangman.HangmanCommandGroup;
import com.c3po.command.hangman.game.HangmanWord;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.command.snakeoil.game.*;
import com.c3po.command.snakeoil.game.card.Card;
import com.c3po.command.snakeoil.game.card.Deck;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.wordnik.WordnikApi;
import com.c3po.core.wordnik.endpoints.GetRandomWords;
import com.c3po.core.wordnik.endpoints.GetWordDefinition;
import com.c3po.core.wordnik.responses.WordResponse;
import com.c3po.service.HumanService;
import com.c3po.ui.input.base.MenuManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnakeOilStartCommand extends SubCommand {
//    private final static List<String> wordCache = new ArrayList<>();

//    private Mono<String> getWord(WordnikApi api) throws Exception {
//        if (!wordCache.isEmpty()) {
//            String word = wordCache.get(0);
//            wordCache.remove(word);
//            return Mono.just(word);
//        } else {
//            return api.call(new GetRandomWords()).flatMap(words->{
//                int i = 0;
//                for(WordResponse word: words.getWords()) {
//                    if (i > 0) {
//                        wordCache.add(word.getWord());
//                    }
//                    i++;
//                }
//                return Mono.just(wordCache.get(0));
//            });
//        }
//    }
//
//    private Mono<Word> getRandomWord() {
//        try {
//            WordnikApi api = new WordnikApi();
//            return getWord(api).flatMap(word -> {
//                try {
//                    return api.call(new GetWordDefinition(word))
//                        .map(definitions -> new Word(word, definitions.getDefinitions().get(0).getText()));
//                } catch (Exception e) {
//                    return Mono.just(new Word("nope", null));
//                }
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void cacheWords() throws Exception {
        WordnikApi api = new WordnikApi();
        api.call(new GetRandomWords())
            .map(words->words.getWords().stream().map(WordResponse::getWord).toList())
        ;
    }



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
                Deck<Profession> professions = new Deck<>(getProfessions().stream().map(Profession::new).toList());
                Deck<Word> deck = new Deck<>(getWords().stream().map(w -> new Word(w, null)).toList());
                GameState gameState = new GameState(players, professions, deck);
                return new SnakeOilGame(gameState, new SnakeOilUI(context));
            })
            .flatMap(SnakeOilGame::start)
            .then();
    }
}
