package com.c3po.command.image;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;

public abstract class ImageSubCommand extends SubCommand {
    protected ImageSubCommand(String name, String description) {
        super(CommandCategory.IMAGE, name, description);
    }
}
