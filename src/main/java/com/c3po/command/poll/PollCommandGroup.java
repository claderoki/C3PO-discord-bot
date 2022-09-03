package com.c3po.command.poll;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class PollCommandGroup extends CommandGroup {
    public PollCommandGroup() {
        super(CommandCategory.POLLS,"Polls");
        addCommand(new PollCreateCommand(this));
    }
}
