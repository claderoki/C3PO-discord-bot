package com.c3po.helper;

import lombok.RequiredArgsConstructor;

import java.util.List;

/** Cycles between values from left to right continuously, starting with 0 again after the last item has been spent.
 * @param <T>
 */
@RequiredArgsConstructor
public final class Cycler<T> {
    private final List<T> values;
    private Integer index = null;

    public Cycler(List<T> values, int index) {
        this(values);
        this.index = index;
    }

    private int getNextIndex(Integer current) {
        if (current == null) {
            return 0;
        }
        if (current == values.size()-1) {
            return 0;
        }
        return current+1;
    }

    private int getNextIndex() {
        return getNextIndex(this.index);
    }

    public T next() {
        index = getNextIndex();
        return values.get(index);
    }
}
