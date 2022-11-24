package com.c3po.command.profile;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import com.c3po.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProfileSubCommand extends SubCommand {
    @Autowired
    protected ProfileService profileService;

    protected ProfileSubCommand(String name, String description) {
        super(CommandCategory.PROFILE, name, description);
    }
}
