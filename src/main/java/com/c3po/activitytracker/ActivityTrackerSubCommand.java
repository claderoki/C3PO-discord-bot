package com.c3po.activitytracker;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import com.c3po.core.command.validation.CommandValidation;
import com.c3po.core.command.validation.GuildOnly;
import com.c3po.core.command.validation.HasPermissions;

import java.util.List;

public abstract class ActivityTrackerSubCommand extends SubCommand {
    protected ActivityTrackerSubCommand(String name, String description) {
        super(CommandCategory.ACTIVITY_TRACKER, name, description);
    }

    @Override
    public List<CommandValidation> getValidations() {
        return List.of(
            HasPermissions.admin(),
            new GuildOnly()
        );
    }
}
