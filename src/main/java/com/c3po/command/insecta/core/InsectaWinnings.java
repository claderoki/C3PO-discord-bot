package com.c3po.command.insecta.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class InsectaWinnings {
    private final Map<Insecta, Long> values;

    public void add(InsectaWinnings winnings) {
        for(var entrySet: winnings.getValues().entrySet()) {
            add(entrySet.getKey(), entrySet.getValue());
        }
    }

    public void add(Insecta insecta, long amount) {
        values.putIfAbsent(insecta, 0L);
        values.computeIfPresent(insecta, (c, v) -> v + amount);
    }
}
