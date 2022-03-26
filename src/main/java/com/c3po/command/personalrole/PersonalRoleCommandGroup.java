package com.c3po.command.personalrole;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class PersonalRoleCommandGroup extends CommandGroup {
    public PersonalRoleCommandGroup() {
        super(CommandCategory.MILKYWAY,"personalrole","Personal role");
        addCommand(new PersonalRoleColorCommand(this));
        addCommand(new PersonalRoleNameCommand(this));
        addCommand(new PersonalRoleDeleteCommand(this));
    }
}
