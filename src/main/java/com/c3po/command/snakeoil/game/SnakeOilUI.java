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
        if (gameState.getChosenProfession() != null) {
            embed.addField(EmbedCreateFields.Field.of("Profession", gameState.getChosenProfession().name(), false));
        }
        int i = 0;
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            StringBuilder value = new StringBuilder();
            TurnStatus status = player.getTurnStatus();
            if (status == TurnStatus.FINISHED) {
                value.append(" [ok]");
            }
            value.append(" [").append(player.getScore());
            if (player == gameState.getPreviousWinner()) {
                value.append("+1");
            }
            value.append("]");
            String name = "[" + (i+1) + "] " + player.getUser().getUsername();
            EmbedCreateFields.Field field = EmbedCreateFields.Field.of(name, value.toString(), false);
            embed.addField(field);
            i++;
        }
        embed.description("snake oil");
    }

}
