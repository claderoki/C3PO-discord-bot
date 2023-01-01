package com.c3po.model.keypool;

public class OutOfKeysException extends Exception {
    OutOfKeysException() {
        super("Out of keys");
    }
}
