package com.c3po.core;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@NoArgsConstructor
public class AccessControlList<T> {
    @Setter
    private AccessControlListMode mode = AccessControlListMode.ALLOW_EXPLICITLY_ALLOWED;
    private final ArrayList<T> allowed = new ArrayList<>();
    private final ArrayList<T> denied = new ArrayList<>();

    public AccessControlList(AccessControlListMode mode) {
        this.mode = mode;
    }

    public final boolean isAllowed(T value) {
        return switch (mode) {
            case ALLOW_UNLESS_DENIED -> !denied.contains(value);
            case ALLOW_EXPLICITLY_ALLOWED -> allowed.contains(value) && !denied.contains(value);
        };
    }

    @SafeVarargs
    public final void allow(T... values) {
        allowed.addAll(Arrays.asList(values));
    }

    public final void allow(T value) {
        allowed.add(value);
    }

    @SafeVarargs
    public final void deny(T... values) {
        denied.addAll(Arrays.asList(values));
    }

    public final void deny(T value) {
        denied.add(value);
    }

}
