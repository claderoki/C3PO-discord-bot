package com.c3po.model.pigeon;

public enum PigeonStatus {
    IDLE,
    MAILING,
    EXPLORING,
    FIGHTING,
    DATING,
    SPACE_EXPLORING,
    JAILED;

    public String toString() {
        return name().toLowerCase();
    }
}