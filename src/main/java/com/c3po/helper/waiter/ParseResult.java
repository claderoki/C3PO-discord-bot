package com.c3po.helper.waiter;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParseResult<T> {
    private ResultType type;
    private List<String> errors = new ArrayList<>();
    private T value;

    public void addError(String message) {
        errors.add(message);
    }
}
