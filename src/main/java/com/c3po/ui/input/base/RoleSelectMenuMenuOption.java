package com.c3po.ui.input.base;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;

import java.util.List;

public class RoleSelectMenuMenuOption extends EntitySelectMenuMenuOption<Role> {
    public RoleSelectMenuMenuOption(String name) {
        super(name);
    }

    @Override
    protected List<Role> fetchOptions() {
        return context.getEvent().getInteraction()
            .getGuild()
            .flux()
            .flatMap(Guild::getRoles)
            .toStream()
            .toList();
    }

    @Override
    protected String toLabel(Role option) {
        return option.getName();
    }
}
