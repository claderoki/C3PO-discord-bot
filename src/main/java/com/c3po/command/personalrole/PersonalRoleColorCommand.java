package com.c3po.command.personalrole;

import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.service.AttributeService;
import reactor.core.publisher.Mono;

public class PersonalRoleColorCommand extends SubCommand {
    protected static Integer personalRoleAttributeId = new AttributeService().getId("required_role_id");

    protected PersonalRoleColorCommand(PersonalRoleCommandGroup group) {
        super(group, "color", "Setup the color of your role.");
        this.addOption(option -> option.name("color")
            .description("The color of your role.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        String rawColor = context.getOptions().getString("color");

        PersonalRoleProcessor processor = new PersonalRoleProcessor(PersonalRoleType.COLOR, rawColor, context);
        return processor.execute();
    }

}
