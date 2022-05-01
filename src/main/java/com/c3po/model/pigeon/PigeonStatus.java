package com.c3po.model.pigeon;

import java.util.Locale;

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