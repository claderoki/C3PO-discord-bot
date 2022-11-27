package com.c3po.helper.waiter2;

import lombok.Builder;
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@Builder
public class RangeFilter {
    private @Nullable Integer min;
    private @Nullable Integer max;

    public boolean isTooHigh(int length) {
        if (max == null) {
            return false;
        }
        return length > max;
    }

    public void ifTooHigh(int length, Consumer<@NonNull Integer> consumer) {
        if (isTooHigh(length)) {
            consumer.accept(Objects.requireNonNull(max));
        }
    }

    public boolean isTooLow(int length) {
        if (min == null) {
            return false;
        }
        return length < min;
    }

    public void ifTooLow(int length, Consumer<@NonNull Integer> consumer) {
        if (isTooLow(length)) {
            consumer.accept(Objects.requireNonNull(min));
        }
    }
}
