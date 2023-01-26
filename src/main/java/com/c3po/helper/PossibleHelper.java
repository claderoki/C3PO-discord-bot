package com.c3po.helper;

import discord4j.discordjson.possible.Possible;

import java.util.Optional;

public class PossibleHelper {
    public static <T> Possible<T> fromOptional(Optional<T> opt) {
        return opt.map(Possible::of).orElseGet(Possible::absent);
    }
}
