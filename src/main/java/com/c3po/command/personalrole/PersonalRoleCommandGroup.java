package com.c3po.command.personalrole;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class PersonalRoleCommandGroup extends CommandGroup {
    public PersonalRoleCommandGroup() {
        super(CommandCategory.MILKYWAY,"personalrole","Personal role");
        addCommand(new PersonalRoleColorCommand(this));
        addCommand(new PersonalRoleNameCommand(this));
        addCommand(new PersonalRoleDeleteCommand(this));
    }
}
