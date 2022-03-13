package com.c3po.command.milkyway;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class MilkywayCommandGroup extends CommandGroup {
    public MilkywayCommandGroup() {
        super(CommandCategory.MILKYWAY,"milkyway","Milkies",DiscordCommandOptionType.SUB_COMMAND_GROUP);
        this.addCommand(new MilkywayAcceptCommand(this));
        this.addCommand(new MilkywayDenyCommand(this));
        this.addCommand(new MilkywayCreateCommand(this));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        throw new IllegalStateException("Shouldn't be called directly.");
    }
}
