package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class PersonalRoleNameCommand extends SubCommand {
    protected PersonalRoleNameCommand(PersonalRoleCommandGroup group) {
        super(group, "name", "Setup the name of your role.");
        this.addOption(option -> option.name("name")
            .description("The name of your role.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        String name = context.getOptions().getString("name");
        PersonalRoleProcessor processor = new PersonalRoleProcessor(PersonalRoleType.NAME, name, context);
        return processor.execute();
    }

}
