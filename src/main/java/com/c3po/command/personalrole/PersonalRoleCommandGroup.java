package com.c3po.command.personalrole;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class PersonalRoleCommandGroup extends CommandGroup {
    public PersonalRoleCommandGroup() {
        super(CommandCategory.MILKYWAY,"personalrole","Personal role",DiscordCommandOptionType.SUB_COMMAND_GROUP);
        addCommand(new PersonalRoleColorCommand(this));
        addCommand(new PersonalRoleNameCommand(this));
        addCommand(new PersonalRoleDeleteCommand(this));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        throw new IllegalStateException("Shouldn't be called directly.");
    }
}
