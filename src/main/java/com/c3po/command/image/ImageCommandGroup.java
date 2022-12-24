package com.c3po.command.image;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class ImageCommandGroup extends CommandGroup {
    public ImageCommandGroup(ImageCreateCommand pollCreateCommand) {
        super(CommandCategory.IMAGE,"Image");
        addCommand(pollCreateCommand);
    }
}
