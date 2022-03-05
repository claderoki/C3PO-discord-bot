package com.c3po.model;

import lombok.Getter;

@Getter
public enum PurchaseType {
    POINT("point"),
    ITEM("item"),
    NONE("none");

    private final String type;
    PurchaseType(String type) {
        this.type = type;
    }

    public static PurchaseType find(String type) {
        for(PurchaseType value: values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("Enum not found.");
    }
}
