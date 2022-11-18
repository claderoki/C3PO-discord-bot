package com.c3po.activitytracker;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class ActivityTrackerCommandGroup extends CommandGroup {
    public ActivityTrackerCommandGroup(ActivityTrackerManageCommand manage) {
        super(CommandCategory.ACTIVITY_TRACKER,"Activity Tracker");
        this.addCommands(manage);
    }
}
