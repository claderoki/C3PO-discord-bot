package com.c3po.helper.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Mode {
    PRODUCTION("production"),
    DEVELOPMENT("development");

    private final String type;

    public static Mode find(String type) {
        for(Mode value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
