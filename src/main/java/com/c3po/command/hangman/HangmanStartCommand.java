package com.c3po.command.hangman;

import com.c3po.command.hangman.game.*;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.wordnik.WordnikApi;
import com.c3po.core.wordnik.endpoints.GetRandomWords;
import com.c3po.core.wordnik.endpoints.GetWordDefinition;
import com.c3po.core.wordnik.responses.WordListResponse;
import com.c3po.core.wordnik.responses.WordResponse;
import com.c3po.service.HumanService;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class HangmanStartCommand extends SubCommand {
    private final HumanService humanService;
    private final AutowireCapableBeanFactory beanFactory;

    private final static List<String> wordCache = new ArrayList<>();
    private final static int bet = 25;

    protected HangmanStartCommand(HumanService humanService, AutowireCapableBeanFactory beanFactory) {
        super(CommandCategory.HANGMAN, "start", "no description");
        this.humanService = humanService;
        this.beanFactory = beanFactory;
    }

    private Mono<Set<User>> getUsers(Context context) {
        LobbyMenu menu = new LobbyMenu(context, bet);
        return new MenuManager<>(menu).waitFor()
            .then(Mono.just(menu.getUsers()));
    }

    private Mono<String> getWord(WordnikApi api) {
        if (!wordCache.isEmpty()) {
            String word = wordCache.get(0);
            wordCache.remove(word);
            return Mono.just(word);
        } else {
            return api.call(new GetRandomWords())
                .map(WordListResponse::getWords)
                .flux()
                .flatMap(Flux::fromIterable)
                .map(WordResponse::getWord)
                .filter(Objects::nonNull)
                .doOnEach(w -> wordCache.add(w.get()))
                .then(Mono.defer(() -> getWord(api)));
        }
    }

    private Mono<HangmanWord> getHangmanWord() {
        WordnikApi api = new WordnikApi();
        return getWord(api).flatMap(word -> api.call(new GetWordDefinition(word))
            .map(definitions -> HangmanWord.builder()
                .value(word.toLowerCase())
                .uneditedValue(word)
                .description(definitions.getDefinitions().get(0).getText())
                .build()));
    }

    private HangmanPlayer toPlayer(User user) {
        HangmanPlayer player = new HangmanPlayer(user, humanService.getHumanId(user.getId()));
        player.setBet(bet);
        return player;
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        return getUsers(context)
            .map(users -> users.stream().map(this::toPlayer).toList())
            .filter(users -> !users.isEmpty())
            .flatMap(players -> getHangmanWord()
                .map(word -> {
                    HangmanGame game = new HangmanGame(word, new HangmanUI(context), new GameState(players));
                    beanFactory.autowireBean(game);
                    return game;
                })
                .flatMap(HangmanGame::start))
            .then();
    }
}
