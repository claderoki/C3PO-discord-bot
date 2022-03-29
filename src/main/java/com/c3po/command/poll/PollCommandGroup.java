package com.c3po.command.poll;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import reactor.core.publisher.Mono;

public class PollCommandGroup extends CommandGroup {
    public PollCommandGroup() {
        super(CommandCategory.POLLS,"poll","Polls");
        addCommand(new PollCreateCommand(this));
    }
}
