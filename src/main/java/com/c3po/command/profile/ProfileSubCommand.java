package com.c3po.command.profile;

import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.SubCommand;
import com.c3po.service.ProfileService;

public abstract class ProfileSubCommand extends SubCommand {
    protected final ProfileService profileService = new ProfileService();

    protected ProfileSubCommand(CommandGroup group, String name, String description) {
        super(group, name, description);
    }
}
