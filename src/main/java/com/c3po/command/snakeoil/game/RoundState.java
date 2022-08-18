package com.c3po.command.snakeoil.game;

import com.c3po.command.snakeoil.game.card.Card;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class RoundState {
    private final HashMap<SnakeOilPlayer, ArrayList<Word>> words = new HashMap<>();
    private SnakeOilPlayer winner;
    private SnakeOilPlayer king;
    private Profession profession;

    public void addWord(SnakeOilPlayer player, Word word) {
        words.computeIfAbsent(player, c -> new ArrayList<>())
            .add(word);
    }

    public List<Word> getWords(SnakeOilPlayer player) {
        return words.get(player);
    }
}
