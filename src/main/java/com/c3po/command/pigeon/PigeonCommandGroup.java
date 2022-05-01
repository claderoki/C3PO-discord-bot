package com.c3po.command.pigeon;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class PigeonCommandGroup extends CommandGroup {
    public PigeonCommandGroup() {
        super(CommandCategory.PIGEON,"pigeon","Pigeons");
        addCommand(new PigeonProfileCommand(this));
        addCommand(new PigeonExploreCommand(this));
    }
}
