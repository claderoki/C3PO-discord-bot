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
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HangmanUI extends UI {
    private final Context context;
    @Setter
    private GameState state;
    private Snowflake messageId;
    private int unrelatedMessages = 0;
    private Message mentionMessage;

    private EmbedCreateSpec getEmbed() {
        EmbedCreateSpec.Builder embed = EmbedHelper.base();
        embed.title(state.getBoard().stream().map(Object::toString).collect(Collectors.joining(" ")));
        List<Guess> allGuesses = new ArrayList<>();
        for(HangmanPlayer player: state.getPlayers()) {
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

    private Mono<Void> processMentionMessage() {
        if (state.getCurrentPlayer() == null) {
            return Mono.empty();
        }

        return Mono.defer(() -> {
            if (mentionMessage != null) {
                return mentionMessage.delete().onErrorResume(Throwable.class, c->Mono.empty()).then();
            }
            return Mono.empty();
        }).then(context.getEvent().createFollowup()
            .withContent(state.getCurrentPlayer().getUser().getMention() + ", your turn!")
            .flatMap(m -> {
                mentionMessage = m;
                return Mono.empty();
            }));
    }

    public Mono<Void> showBoard() {
        EmbedCreateSpec embed = getEmbed();
        if (messageId == null || unrelatedMessages >= 5) {
            return context.getEvent().createFollowup()
                .withContent(state.getCurrentPlayer() == null ? "" : state.getCurrentPlayer().getUser().getMention())
                .withEmbeds(embed)
                .flatMap(message -> {
                    messageId = message.getId();
                    return Mono.empty();
                }).then(processMentionMessage());
        } else {
            return context.getEvent().editFollowup(messageId)
                .withContentOrNull(state.getCurrentPlayer() == null ? null : state.getCurrentPlayer().getUser().getMention())
                .withEmbeds(embed)
                .then(processMentionMessage());
        }
    }

    private boolean isGuessAllowed(MessageCreateEvent event) {
        if (event.getMessage().getAuthor().isEmpty()) {
            return false;
        }
        User user = event.getMessage().getAuthor().get();
        return state.getCurrentPlayer().getUser().equals(user);
    }

    private boolean preCheck(MessageCreateEvent event) {
        if (!context.getEvent().getInteraction().getChannelId().equals(event.getMessage().getChannelId())) {
            return false;
        }
        return event.getMessage().getAuthor().map(u -> !u.isBot()).orElse(false);
    }

    public Flux<Guess> waitForGuesses() {
        return context.getEvent().getClient().on(MessageCreateEvent.class)
            .filter(this::preCheck)
            .map(c -> {
                unrelatedMessages++;
                return c;
            })
            .filter(this::isGuessAllowed)
            .map(MessageCreateEvent::getMessage)
            .filter(m -> m.getContent().length() == 1 || m.getContent().length() == state.getBoard().size())
            .flatMap(m -> {
                GuessType type = m.getContent().length() == 1 ? GuessType.LETTER : GuessType.WORD;
                return m.delete().then(Mono.just(new Guess(type, m.getContent().toLowerCase())));
            })
            .filter(guess -> state.getGuesses().stream().noneMatch(g -> g.getValue().equals(guess.getValue())))
            .map(c -> {
                unrelatedMessages--;
                return c;
            })
            .timeout(Duration.ofSeconds(360))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
        ;
    }

    public Mono<Void> showEndGame(EmbedCreateSpec build) {
        return context.getEvent().createFollowup().withEmbeds(build).then();
    }
}
