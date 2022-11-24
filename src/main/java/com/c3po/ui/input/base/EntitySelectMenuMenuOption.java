package com.c3po.ui.input.base;

import discord4j.core.object.entity.Entity;

public abstract class EntitySelectMenuMenuOption<E extends Entity> extends SelectMenuMenuOption<E> {
    public EntitySelectMenuMenuOption(String name) {
        super(name);
    }

    @Override
    protected String toValue(E option) {
        return option.getId().toString();
    }
}
