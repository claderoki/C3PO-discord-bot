package com.c3po.command.hangman.game;

import com.c3po.command.hangman.game.core.UI;
import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HangmanUI extends UI {
    private final Context context;
    private Snowflake messageId;
    private int unrelatedMessages = 0;
    private Message mentionMessage;

    public void showEndGame(EmbedCreateSpec embed) {
        context.getEvent().createFollowup().withEmbeds(embed).subscribe();
    }

    private EmbedCreateSpec getEmbed(List<Character> board, List<HangmanPlayer> players) {
        EmbedCreateSpec.Builder embed = EmbedHelper.base();
        embed.title(board.stream().map(Object::toString).collect(Collectors.joining(" ")));
        List<Guess> allGuesses = new ArrayList<>();
        for(HangmanPlayer player: players) {
            allGuesses.addAll(player.getGuesses());
            if (!player.isDead()) {
                int state = (int)player.getGuesses().stream().filter(c -> c.getWorth() == 0).count();
                embed.addField(player.getUser().getUsername(), ">>> ```\n" + HangmanStateHelper.getState(state) + "```", true);
            }
        }

        if (!allGuesses.isEmpty()) {
            String letters = allGuesses.stream().filter(g -> g.getType().equals(GuessType.LETTER)).map(g -> "**" + g.getValue() + "**").collect(Collectors.joining(", "));
            String words = allGuesses.stream().filter(g -> g.getType().equals(GuessType.WORD)).map(Guess::getValue).collect(Collectors.joining(", "));

            String value = "letters used: " + letters;
            if (!words.isEmpty()) {
                value += "\nwords used: " + words;
            }

            embed.addField("\uFEFF", value, false);
        }
        return embed.build();
    }

    public void showBoard(List<Character> board, List<HangmanPlayer> players, HangmanPlayer currentPlayer) {
        if (currentPlayer != null) {
            if (mentionMessage != null) {
                mentionMessage.delete().onErrorResume(Throwable.class, c->Mono.empty()).subscribe();
            }
            context.getEvent().createFollowup()
                .withContent(currentPlayer.getUser().getMention() + ", your turn!")
                .subscribe(m -> {
                    mentionMessage = m;
                    m.delete().delaySubscription(Duration.ofSeconds(60)).subscribe();
                });
        }

        EmbedCreateSpec embed = getEmbed(board, players);
        if (messageId == null || unrelatedMessages >= 5) {
            var message = context.getEvent().createFollowup()
                .withContent(currentPlayer == null ? "" : currentPlayer.getUser().getMention())
                .withEmbeds(embed)
                .blockOptional()
                .orElseThrow();
            messageId = message.getId();
        } else {
            context.getEvent().editFollowup(messageId)
                .withContentOrNull(currentPlayer == null ? null : currentPlayer.getUser().getMention())
                .withEmbeds(embed)
                .block();
        }
    }

    private void sendError(User user, String message) {
        context.getEvent().createFollowup()
            .withEmbeds(EmbedHelper.error(user.getMention() + ", " + message).build())
            .subscribe(m -> m.delete().delaySubscription(Duration.ofSeconds(5)).subscribe());
    }

    public Guess waitForGuess(HangmanPlayer player, List<Character> board, List<Guess> guesses) {
        return context.getEvent().getClient().on(MessageCreateEvent.class)
            .map(c -> {
                unrelatedMessages++;
                return c;
            })
            .filter(c -> c.getMessage().getAuthor().isPresent() && c.getMessage().getAuthor().get().getId().equals(player.getUser().getId()))
            .map(MessageCreateEvent::getMessage)
            .filter(m -> m.getContent().length() == 1 || m.getContent().length() == board.size())
            .map(m -> {
                m.delete().subscribe();
                return m.getContent().toLowerCase();
            })
            .filter(c -> {
                if (guesses.stream().anyMatch(g -> g.getValue().equals(c))) {
                    sendError(player.getUser(), c + " has already been used.");
                    return false;
                }
                return true;
            })
            .map(c -> {
                GuessType type = c.length() == 1 ? GuessType.LETTER : GuessType.WORD;
                Guess guess = new Guess(type, c);
                return guess;
            })
            .map(c -> {
                unrelatedMessages--;
                return c;
            })
            .timeout(Duration.ofSeconds(60))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .next()
            .block()
        ;
    }

}
