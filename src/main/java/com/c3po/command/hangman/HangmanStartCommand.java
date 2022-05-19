package com.c3po.command.hangman;

import com.c3po.command.hangman.game.*;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.wordnik.WordnikApi;
import com.c3po.core.wordnik.endpoints.GetRandomWords;
import com.c3po.core.wordnik.endpoints.GetWordDefinition;
import com.c3po.core.wordnik.responses.WordResponse;
import com.c3po.service.HumanService;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.*;

public class HangmanStartCommand extends SubCommand {
    private final HumanService humanService = new HumanService();
    private final static List<String> wordCache = new ArrayList<>();
    private final static int bet = 25;

    protected HangmanStartCommand(HangmanCommandGroup group) {
        super(group, "start", "no description");
    }

    private Mono<Set<User>> getUsers(Context context) {
        LobbyMenu menu = new LobbyMenu(context, bet);
        return MenuManager.waitForMenu(menu)
            .then(Mono.just(menu.getUsers()));
    }

    private Mono<String> getWord(WordnikApi api) throws Exception {
        if (!wordCache.isEmpty()) {
            String word = wordCache.get(0);
            wordCache.remove(word);
            return Mono.just(word);
        } else {
            return api.call(new GetRandomWords()).flatMap(words->{
                int i = 0;
                for(WordResponse word: words.getWords()) {
                    if (i > 0) {
                        wordCache.add(word.getWord());
                    }
                    i++;
                }
                return Mono.just(wordCache.get(0));
            });
        }
    }

    private Mono<HangmanWord> getHangmanWord() {
        try {
            WordnikApi api = new WordnikApi();
            return getWord(api).flatMap(word -> {
                try {
                return api.call(new GetWordDefinition(word))
                    .map(definitions -> HangmanWord.builder()
                        .value(word.toLowerCase())
                        .uneditedValue(word)
                        .description(definitions.getDefinitions().get(0).getText())
                        .build());
                } catch (Exception e) {
                    return Mono.just(HangmanWord.builder().build());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HangmanPlayer toPlayer(User user) {
        HangmanPlayer player = new HangmanPlayer(user, humanService.getHumanId(user.getId()));
        player.setBet(bet);
        return player;
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        return getUsers(context)
            .map(users -> users.stream().map(this::toPlayer).toList())
            .filter(users -> !users.isEmpty())
            .flatMap(players -> getHangmanWord()
                .map(word -> new HangmanGame(word, new HangmanUI(context), new GameState(players)))
                .flatMap(HangmanGame::start))
            .then();
    }
}
