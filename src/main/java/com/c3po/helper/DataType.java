package com.c3po.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataType {
    INTEGER("integer"),
    STRING("string"),
    BOOLEAN("boolean"),
    CHANNEL("channel"),
    CATEGORY("category");

    private final String type;

    public static DataType find(String type) {
        for(DataType value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }

}
