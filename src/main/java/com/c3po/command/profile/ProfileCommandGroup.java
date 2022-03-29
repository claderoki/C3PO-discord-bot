package com.c3po.command.profile;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class ProfileCommandGroup extends CommandGroup {
    public ProfileCommandGroup() {
        super(CommandCategory.PROFILE,"profile","Profile info");
        addCommand(new ProfileViewCommand(this));
        addCommand(new ProfileSetupCommand(this));
    }
}
