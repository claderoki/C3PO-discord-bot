package com.c3po.command.insecta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class InsectaWinning {
    private final String key;
    private final long userId;
    private final long value;
    private boolean collected;
}
