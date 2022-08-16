package com.c3po.command.snakeoil.game;

import com.c3po.core.command.Context;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SnakeOilUI {
    @Getter
    private final Context context;

    public void getEmbed(GameState gameState, EmbedCreateSpec.Builder embed) {
        StringBuilder description = new StringBuilder("snake oil game");
        if (gameState.getChosenProfession() != null) {
            embed.addField(EmbedCreateFields.Field.of("Profession", gameState.getChosenProfession().name(), false));
        }
        int i = 0;
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            description.append("\n").append("[").append(i+1).append("]").append(" ")
                .append(player.user().getUsername())
            ;
            TurnStatus status = gameState.getStatuses().get(player);
            if (status == TurnStatus.FINISHED) {
                description.append(" [ok]");
            }

            if (player == gameState.getPreviousWinner()) {
                description.append(" [+1]");
            }
            i++;
        }
        embed.description(description.toString());
    }

}
