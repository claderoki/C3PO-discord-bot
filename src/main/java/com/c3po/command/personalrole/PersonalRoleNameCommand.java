package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import org.springframework.stereotype.Component;

@Component
public class PersonalRoleNameCommand extends PersonalRoleSubCommand {
    protected PersonalRoleNameCommand() {
        super("name", "Setup the name of your role.");
        this.addOption(option -> option.name("name")
            .description("The name of your role.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    @Override
    protected PersonalRoleProcessor getProcessor(Context context) {
        String name = context.getOptions().getString("name");
        return new PersonalRoleProcessor(PersonalRoleType.NAME, name, context);
    }
}
