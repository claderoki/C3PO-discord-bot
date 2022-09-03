package com.c3po.helper;

import java.util.List;

/** Cycles between values continuously, starting over after the last item has been spent.
 * @param <T>
 */
public final class Cycler<T> {
    private final List<T> values;
    private final CycleDirection direction;
    private Integer index;

    public Cycler(List<T> values, Integer index, CycleDirection direction) {
        this.values = values;
        this.index = index;
        this.direction = direction;
    }

    public Cycler(List<T> values, Integer index) {
        this(values, index, CycleDirection.LEFT_TO_RIGHT);
    }

    public Cycler(List<T> values, CycleDirection direction) {
        this(values, null, direction);
    }

    public Cycler(List<T> values) {
        this(values, (Integer) null);
    }

    private int getStartPosition() {
        return direction == CycleDirection.LEFT_TO_RIGHT ? 0 : values.size()-1;
    }

    private int getEndPosition() {
        return direction == CycleDirection.LEFT_TO_RIGHT ? values.size()-1 : 0;
    }

    private int getIncrement() {
        return direction == CycleDirection.LEFT_TO_RIGHT ? 1 : -1;
    }

    private int getNextIndex() {
        if (index == null || index == getEndPosition()) {
            return getStartPosition();
        }
        return index+getIncrement();
    }

    public T next() {
        index = getNextIndex();
        return values.get(index);
    }
}
