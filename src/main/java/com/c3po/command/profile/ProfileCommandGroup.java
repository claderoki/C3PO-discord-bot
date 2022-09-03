package com.c3po.command.profile;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileCommandGroup extends CommandGroup {

    @Autowired
    public ProfileCommandGroup(ProfileViewCommand view, ProfileSetupCommand setup) {
        super(CommandCategory.PROFILE,"Profile info");
        addCommand(view);
        addCommand(setup);
    }
}
