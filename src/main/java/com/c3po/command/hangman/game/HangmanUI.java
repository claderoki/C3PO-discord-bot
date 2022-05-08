package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.UI;
import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HangmanUI extends UI {
    private final Context context;

    protected final static String[] states = {"""
  ╤═══╗
      ║
      ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
      ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
  │   ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
  │\\  ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
   \\  ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
 / \\  ║
      ║
══════╩═
"""};

    private EmbedCreateSpec getEmbed(List<Character> board, List<HangmanPlayer> players) {
        EmbedCreateSpec.Builder embed = EmbedHelper.base();
        embed.title(board.stream().map(Object::toString).collect(Collectors.joining(" ")));
        List<Guess> allGuesses = new ArrayList<>();
        for(HangmanPlayer player: players) {
            allGuesses.addAll(player.getGuesses());
            if (!player.isDead()) {
                int state = (int)player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
                embed.addField(player.getUser().getUsername(), ">>> ```\n" + states[state] + "```", true);
            }
        }

        if (!allGuesses.isEmpty()) {
            String letters = allGuesses.stream().filter(g -> g.getType().equals(GuessType.LETTER)).map(g -> "**" + g.getValue() + "**").collect(Collectors.joining(", "));
            String words = allGuesses.stream().filter(g -> g.getType().equals(GuessType.WORD)).map(Guess::getValue).collect(Collectors.joining(", "));

            String value = "letters used: " + letters;
            if (!words.isEmpty()) {
                value += "\nwords used: " + words;
            }

            embed.addField("\uFEFF", value, true);
        }
        return embed.build();
    }

    public void showBoard(List<Character> board, List<HangmanPlayer> players, HangmanPlayer currentPlayer) {
        if (currentPlayer != null) {
            context.getEvent().createFollowup()
                .withContent(currentPlayer.getUser().getMention() + ", your turn!")
                .subscribe(m -> m.delete().delaySubscription(Duration.ofSeconds(5)).subscribe());
        }

        EmbedCreateSpec embed = getEmbed(board, players);
        context.getEvent().editReply()
            .withContentOrNull(currentPlayer == null ? null : currentPlayer.getUser().getMention())
            .withEmbedsOrNull(Collections.singleton(embed))
            .block();
    }

    public Guess waitForGuess(HangmanPlayer player, List<Character> board) {
        return context.getEvent().getClient().on(MessageCreateEvent.class)
            .filter(c -> c.getMessage().getAuthor().isPresent() && c.getMessage().getAuthor().get().getId().equals(player.getUser().getId()))
            .map(c -> {
                c.getMessage().delete().subscribe();
                return c.getMessage().getContent();
            })
            .filter(c -> c.length() == 1 || c.length() == board.size())
            .map(c -> {
                GuessType type = c.length() == 1 ? GuessType.LETTER : GuessType.WORD;
                return new Guess(type, c);
            })
            .filter(c -> c.getType().equals(GuessType.WORD) || !board.contains(c.getValue().charAt(0)))
            .timeout(Duration.ofSeconds(60))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .next()
            .block()
        ;
    }

}
