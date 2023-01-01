package com.c3po.model.keypool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Key {
    @Setter
    private boolean valid;
    private final String value;
}