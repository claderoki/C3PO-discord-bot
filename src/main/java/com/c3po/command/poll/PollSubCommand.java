package com.c3po.command.poll;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;

public abstract class PollSubCommand extends SubCommand {
    protected PollSubCommand(String name, String description) {
        super(CommandCategory.POLLS, name, description);
    }
}
