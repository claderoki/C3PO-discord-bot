package com.c3po.helper.waiter;

import com.c3po.error.PublicException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Deprecated
public class ParseResult<T> {
    private ResultType type;
    private List<String> errors = new ArrayList<>();
    private T value;

    public void addError(String message) {
        errors.add(message);
    }

    public T getValueOrThrow() throws PublicException {
        if (type.equals(ResultType.ERROR)) {
            throw new PublicException("Error(s): " + String.join(", ", errors));
        }
        return value;
    }
}
