package com.c3po.model;

import lombok.Getter;

@Getter
public enum MilkywayStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DENIED("denied"),
    EXPIRED("expired");

    private final String type;
    MilkywayStatus(String type) {
        this.type = type;
    }

    public static MilkywayStatus find(String type) {
        for(MilkywayStatus value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
