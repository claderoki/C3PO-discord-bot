package com.c3po.command.snakeoil.game;

import com.c3po.command.snakeoil.game.card.Card;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.core.command.Context;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SnakeOilUI {
    @Getter
    private final Context context;

    public void getEmbed(GameState gameState, EmbedCreateSpec.Builder embed) {
        int i = 0;
        for(SnakeOilPlayer player: gameState.getPlayers()) {
            StringBuilder value = new StringBuilder();
            value.append(" Wins: ")
                .append(player.getScore())
            ;
            List<Word> words = gameState.getCurrentRound().getWords(player);
            if (words != null) {
                value.append(" ").append(String.join(", ", words.stream().map(Card::getValue).toList()));
            }

            String name = " [" + (i+1) + "] " + player.getUser().getUsername();
            EmbedCreateFields.Field field = EmbedCreateFields.Field.of(name, value.toString(), false);
            embed.addField(field);
            i++;
        }
        embed.description("snake oil");

        if (gameState.getCurrentRound().getProfession() != null) {
            embed.footer("profession: " + gameState.getCurrentRound().getProfession().getValue(), null);
        }
    }

}
