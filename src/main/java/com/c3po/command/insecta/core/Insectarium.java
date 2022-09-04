package com.c3po.command.insecta.core;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Insectarium {
    private final Map<Insecta, Long> values;
}
