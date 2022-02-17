package com.c3po.ui;

public class WaiterFactory {
    public static Waiter<?> getFor(Object from) {
        switch (from.getClass().getName()) {
            default -> {
                String a = "";
            }
        }

        throw new RuntimeException("not found");
    }
}
