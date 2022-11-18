package com.c3po.activitytracker;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandSettings;
import com.c3po.core.command.SubCommand;

public abstract class ActivityTrackerSubCommand extends SubCommand {
    protected ActivityTrackerSubCommand(String name, String description) {
        super(CommandCategory.ACTIVITY_TRACKER, name, description);
    }

    @Override
    public CommandSettings getSettings() {
        return CommandSettings.builder()
            .guildOnly(true)
            .adminOnly(true)
            .build();
    }
}
