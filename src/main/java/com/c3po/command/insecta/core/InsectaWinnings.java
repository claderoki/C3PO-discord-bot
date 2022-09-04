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
            values.computeIfPresent(entrySet.getKey(), (c, v) -> entrySet.getValue() + v);
            values.computeIfAbsent(entrySet.getKey(), c -> entrySet.getValue());
        }
    }
}
