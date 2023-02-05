package com.c3po.core.command.validation;

import discord4j.rest.util.Permission;

import java.util.Set;

public  class IsAdmin extends HasPermissions {
    @Override
    protected Set<Permission> getRequiredPermissions() {
        return Set.of(Permission.ADMINISTRATOR);
    }
}
