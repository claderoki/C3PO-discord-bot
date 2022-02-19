package com.c3po.helper.setting.validation;

public enum ValueType {
    SETTING("setting")
    ;

    private String type;
    ValueType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ValueType find(String type) {
        for(ValueType value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
