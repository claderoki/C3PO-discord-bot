package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import org.springframework.stereotype.Component;

@Component
public class PersonalRoleColorCommand extends PersonalRoleSubCommand {
    protected PersonalRoleColorCommand() {
        super("color", "Setup the color of your role.");
        this.addOption(option -> option.name("color")
            .description("The color of your role.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    @Override
    protected PersonalRoleProcessor getProcessor(Context context) {
        String rawColor = context.getOptions().getString("color");
        return new PersonalRoleProcessor(PersonalRoleType.COLOR, rawColor, context);
    }

}
