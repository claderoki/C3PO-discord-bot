package com.c3po.helper;

import discord4j.discordjson.possible.Possible;

import java.util.Optional;

public class PossibleParser {
    public static <T> Possible<T> toPossible(T option) {
        return Optional.ofNullable(option).map(Possible::of).orElseGet(Possible::absent);
    }

}
