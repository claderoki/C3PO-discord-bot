package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.game.card.Card;
import com.c3po.helper.Emoji;
import com.c3po.ui.input.base.*;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class ShowDeckButton extends ButtonMenuOption<Void> {
    protected final GameState gameState;
    private final List<Snowflake> playerIds;

    public ShowDeckButton(GameState gameState) {
        super("My deck");
        withEmoji(Emoji.CARD);
        this.gameState = gameState;
        this.playerIds = gameState.getPlayers().stream().map(c -> c.getUser().getId()).toList();
    }

    @Override
    public boolean isAllowed(ButtonInteractionEvent event) {
        return playerIds.contains(event.getInteraction().getUser().getId());
    }

    @Override
    protected boolean isBottomRow() {
        return true;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        SnakeOilPlayer player = gameState.getPlayers().stream()
            .filter(u -> u.getUser().equals(event.getInteraction().getUser()))
            .findFirst()
            .orElseThrow();

        return event.reply()
            .withEphemeral(true)
            .withContent(player.getWords().getCards().stream().map(Card::getValue)
                .collect(Collectors.joining(", ")));
    }

}
