package com.c3po.command.insecta.core;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InsectaFactory {
    private final static Map<String, Insecta> insectas = Stream.of(
        new Mosquito()
    ).collect(Collectors.toMap(Mosquito::getKey, m -> m));

    public static Insecta get(String type) {
        return insectas.get(type);
    }
}
