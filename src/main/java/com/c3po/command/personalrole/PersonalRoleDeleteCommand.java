package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import org.springframework.stereotype.Component;

@Component
public class PersonalRoleDeleteCommand extends PersonalRoleSubCommand {
    protected PersonalRoleDeleteCommand() {
        super("delete", "Delete your own role.");
    }

    @Override
    protected PersonalRoleProcessor getProcessor(Context context) {
        return new PersonalRoleProcessor(PersonalRoleType.DELETE, null, context);
    }

}
