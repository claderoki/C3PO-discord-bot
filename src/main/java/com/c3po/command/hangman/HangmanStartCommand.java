package com.c3po.command.hangman;

import com.c3po.command.hangman.game.HangmanGame;
import com.c3po.command.hangman.game.HangmanPlayer;
import com.c3po.command.hangman.game.HangmanUI;
import com.c3po.command.hangman.game.HangmanWord;
import com.c3po.command.hangman.game.core.LobbyMenu;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.wordnik.WordnikApi;
import com.c3po.core.wordnik.endpoints.GetRandomWords;
import com.c3po.core.wordnik.endpoints.GetWordDefinition;
import com.c3po.core.wordnik.responses.WordDefinitionResponse;
import com.c3po.core.wordnik.responses.WordListResponse;
import com.c3po.core.wordnik.responses.WordResponse;
import com.c3po.service.HumanService;
import com.c3po.ui.input.base.MenuManager;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.*;

public class HangmanStartCommand extends SubCommand {
    private final static List<String> wordCache = new ArrayList<>();
    private final static int bet = 25;

    protected HangmanStartCommand(HangmanCommandGroup group) {
        super(group, "start", "no description");
    }

    private Mono<Set<User>> getUsers(Context context) {
        LobbyMenu menu = new LobbyMenu(context, bet);
        return MenuManager.waitForMenu(menu).then(Mono.just(menu.getUsers()));
    }

    private String getWord(WordnikApi api) throws Exception {
        if (!wordCache.isEmpty()) {
            String word = wordCache.get(0);
            wordCache.remove(word);
            return word;
        } else {
            GetRandomWords getRandomWords = new GetRandomWords();
            WordListResponse words = api.call(getRandomWords).blockOptional().orElseThrow();
            for(WordResponse word: words.getWords()) {
                wordCache.add(word.getWord());
            }
            return getWord(api);
        }
    }

    private HangmanWord getHangmanWord() {
        try {
            WordnikApi api = new WordnikApi();
            String word = getWord(api);
            GetWordDefinition getWordDefinition = new GetWordDefinition(word);
            WordDefinitionResponse definition = api.call(getWordDefinition).blockOptional().orElseThrow().getDefinitions().get(0);

            return HangmanWord.builder()
                .value(word.toLowerCase())
                .uneditedValue(word)
                .description(definition.getText())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        List<HangmanPlayer> players = Objects.requireNonNull(getUsers(context).block())
            .stream()
            .map(u -> {
                HangmanPlayer player = new HangmanPlayer(u, HumanService.getHumanId(u.getId()));
                player.setBet(bet);
                return player;
            })
            .toList();

        if (players.isEmpty()) {
            return Mono.empty();
        }

        HangmanGame game = new HangmanGame(getHangmanWord(), players, new HangmanUI(context));
        game.start();
        return Mono.empty();
    }
}
