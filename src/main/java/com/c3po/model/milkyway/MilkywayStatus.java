package com.c3po.model.milkyway;

import lombok.Getter;

@Getter
public enum MilkywayStatus {
    PENDING,
    ACCEPTED,
    DENIED,
    EXPIRED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
