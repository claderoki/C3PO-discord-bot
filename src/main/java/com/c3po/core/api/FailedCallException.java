package com.c3po.core.api;

import lombok.Getter;

@Getter
public class FailedCallException extends Exception {
    private final String response;
    private final int statusCode;

    public FailedCallException(int statusCode, String response, int retries) {
        super("Call failed with statusCode " + statusCode + " after " + retries + " retries");
        this.response = response;
        this.statusCode = statusCode;
    }

    public FailedCallException(int statusCode, String response) {
        this(statusCode, response, 0);
    }
}
