package com.c3po.command.snakeoil.game;

import com.c3po.game.card.Card;
import com.c3po.command.snakeoil.game.card.Profession;
import com.c3po.command.snakeoil.game.card.Word;
import com.c3po.helper.Cycler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@RequiredArgsConstructor
public class RoundState {
    private final HashMap<SnakeOilPlayer, ArrayList<Word>> words = new HashMap<>();
    private final Cycler<SnakeOilPlayer> cycler;
    private Profession profession;
    private SnakeOilPlayer winner;
    private SnakeOilPlayer customer;

    public void addWord(SnakeOilPlayer player, Word word) {
        words.computeIfAbsent(player, c -> new ArrayList<>())
            .add(word);
    }

    public List<Word> getWords(SnakeOilPlayer player) {
        return words.get(player);
    }

    public String getProduct(SnakeOilPlayer player) {
        return words.get(player).stream().map(Card::getValue).collect(Collectors.joining(" "));
    }

}
