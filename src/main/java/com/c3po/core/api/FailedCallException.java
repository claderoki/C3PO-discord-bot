package com.c3po.core.api;

import lombok.Getter;

@Getter
public class FailedCallException extends Exception {
    private final String response;
    private final int statusCode;

    public FailedCallException(int statusCode, String response) {
        super("Call failed with statusCode " + statusCode);
        this.response = response;
        this.statusCode = statusCode;
    }
}
