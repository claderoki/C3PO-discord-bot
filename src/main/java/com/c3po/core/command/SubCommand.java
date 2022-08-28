package com.c3po.core.command;

import com.c3po.helper.DiscordCommandOptionType;

public abstract class SubCommand extends Command {
    private final String groupName;

    protected SubCommand(CommandGroup group, String name, String description) {
        super(group.getCategory(), name, description, DiscordCommandOptionType.SUB_COMMAND);
        this.groupName = group.getName();
    }

    protected SubCommand(CommandCategory category, String name, String description) {
        super(category, name, description, DiscordCommandOptionType.SUB_COMMAND);
        this.groupName = category.getValue();
    }

    public String getFullName() {
        return "%s %s".formatted(groupName, this.getName());
    }
}
