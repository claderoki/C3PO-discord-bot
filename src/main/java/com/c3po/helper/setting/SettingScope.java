package com.c3po.helper.setting;

import lombok.Getter;

@Getter
public enum SettingScope {
    GUILD("guild"),
    USER("user"),
    MEMBER("member");

    private final String type;

    SettingScope(String type) {
        this.type = type;
    }

    public static SettingScope find(String type) {
        for(SettingScope value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
