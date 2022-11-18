package com.c3po.command.personalrole;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonalRoleCommandGroup extends CommandGroup {
    public PersonalRoleCommandGroup(PersonalRoleColorCommand color, PersonalRoleNameCommand name, PersonalRoleDeleteCommand delete) {
        super(CommandCategory.PERSONAL_ROLE,"Personal role");
        addCommands(color, name, delete);
    }
}
