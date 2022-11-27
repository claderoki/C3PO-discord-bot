package com.c3po.helper.waiter2.parser;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ParseException extends Exception {
    private final List<String> errors;

    public ParseException(String error) {
        this(List.of(error));
    }

    @Override
    public String getMessage() {
        return String.join(", ", errors);
    }
}
