package com.c3po.command.poll;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class PollCommandGroup extends CommandGroup {
    public PollCommandGroup(PollCreateCommand pollCreateCommand) {
        super(CommandCategory.POLLS,"Polls");
        addCommand(pollCreateCommand);
    }
}
