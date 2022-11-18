package com.c3po.core.setting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SettingCategory {
    GUILDREWARDS("guildrewards"),
    MILKYWAY("milkyway"),
    PERSONALROLE("personalrole"),
    DISCONNECTER("disconnecter"),
    ACTIVITY_TRACKER("activitytracker"),
    ;

    private final String type;

    public static SettingCategory find(String type) {
        for(var value: values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Not found.");
    }
}
