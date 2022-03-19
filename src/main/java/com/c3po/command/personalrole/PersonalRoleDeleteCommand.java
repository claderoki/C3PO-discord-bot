package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import reactor.core.publisher.Mono;

public class PersonalRoleDeleteCommand extends SubCommand {
    protected PersonalRoleDeleteCommand(PersonalRoleCommandGroup group) {
        super(group, "delete", "Delete your own role.");
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        PersonalRoleProcessor processor = new PersonalRoleProcessor(PersonalRoleType.DELETE, null, context);
        return processor.execute();
    }

}
