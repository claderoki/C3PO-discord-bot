package com.c3po.ui.input.base;


import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class EnumSelectMenuMenuOption<E extends Enum<E>> extends SelectMenuMenuOption<E> {
    private final Function<E, String> toLabel;
    private final Class<E> cls;

    public EnumSelectMenuMenuOption(String name, Class<E> cls, Function<E, String> toLabel) {
        super(name);
        this.toLabel = toLabel;
        this.cls = cls;
    }

    @SneakyThrows
    @Override
    protected List<E> fetchOptions() {
        E[] values = (E[]) cls.getMethod("values").invoke(null, null);
        return Arrays.stream(values).toList();
    }

    @Override
    protected String toValue(E option) {
        return option.name();
    }

    @Override
    protected String toLabel(E option) {
        return toLabel.apply(option);
    }
}
