package com.c3po.helper.waiter2;

import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

@Builder
@Getter
public class RangeFilter {
    private @Nullable Integer min;
    private @Nullable Integer max;

    public boolean isTooHigh(int length) {
        if (max == null) {
            return false;
        }
        return length > max;
    }

    public boolean isTooLow(int length) {
        if (min == null) {
            return false;
        }
        return length < min;
    }
}
