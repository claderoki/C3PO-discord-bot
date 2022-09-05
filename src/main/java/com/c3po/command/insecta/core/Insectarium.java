package com.c3po.command.insecta.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Insectarium {
    private final HashMap<Insecta, Long> values;

    public Insectarium() {
        this.values = new HashMap<>();
    }

    public void add(Insecta insecta, int amount) {
        add(insecta, (long)amount);
    }

    public void add(Insecta insecta, long amount) {
        values.putIfAbsent(insecta, 0L);
        values.computeIfPresent(insecta, (k, v) -> v + amount);
    }

    public long getCount(Insecta insecta) {
        return values.getOrDefault(insecta, 0L);
    }

}
