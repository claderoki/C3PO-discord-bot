package com.c3po.command.snakeoil.game;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class ShowDeckButton extends ButtonMenuOption<Void> {
    private final GameState gameState;

    public ShowDeckButton(GameState gameState) {
        super("Show deck");
        this.gameState = gameState;
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        User user = event.getInteraction().getUser();
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            if (player.user().equals(user)) {
                return event.reply()
                    .withEphemeral(true)
                    .withContent(player.deck().getCards().stream().map(Card::getWord).collect(Collectors.joining(", ")))
                ;
            }
        }
        return null;
    }
}
