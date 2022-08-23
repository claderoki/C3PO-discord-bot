package com.c3po.core;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
public class AccessControlList<T> {
    @Setter
    private AccessControlListMode mode = AccessControlListMode.ALLOW_EXPLICITLY_ALLOWED;
    private final ArrayList<T> allowed = new ArrayList<>();
    private final ArrayList<T> denied = new ArrayList<>();

    public AccessControlList(AccessControlListMode mode) {
        this.mode = mode;
    }

    public boolean isAllowed(T value) {
        return switch (mode) {
            case ALLOW_UNLESS_DENIED -> !denied.contains(value);
            case ALLOW_EXPLICITLY_ALLOWED -> allowed.contains(value) && !denied.contains(value);
        };
    }

    public void allow(T value) {
        allowed.add(value);
    }

    public void deny(T value) {
        denied.add(value);
    }
}
