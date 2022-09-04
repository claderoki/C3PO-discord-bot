package com.c3po.command.insecta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class InsectaWinningDTO {
    private final String key;
    private final long userId;
    private final long value;
    private boolean collected = false;
}
