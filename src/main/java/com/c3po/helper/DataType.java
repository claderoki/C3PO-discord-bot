package com.c3po.helper;

import lombok.Getter;

@Getter
public enum DataType {
    INTEGER("integer"),
    STRING("string"),
    BOOLEAN("boolean"),
    DURATION("duration");

    private final String type;

    DataType(String type) {
        this.type = type;
    }

    public static DataType find(String type) {
        for(DataType value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }

}
