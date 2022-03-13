package com.c3po.core.command;

import com.c3po.helper.DiscordCommandOptionType;

public abstract class SubCommand extends Command {
    private final CommandGroup group;

    protected SubCommand(CommandGroup group, String name, String description) {
        super(group.getCategory(), name, description, DiscordCommandOptionType.SUB_COMMAND);
        this.group = group;
    }

    public String getFullName() {
        return "%s %s".formatted(this.group.getName(), this.getName());
    }
}
