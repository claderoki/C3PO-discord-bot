package com.c3po.helper.setting.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValueType {
    SETTING("setting");

    private final String type;

    public static ValueType find(String type) {
        for(ValueType value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
