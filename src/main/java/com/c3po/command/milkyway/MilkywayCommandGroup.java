package com.c3po.command.milkyway;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class MilkywayCommandGroup extends CommandGroup {
    public MilkywayCommandGroup() {
        super(CommandCategory.MILKYWAY,"milkyway","Milkies");
        this.addCommand(new MilkywayAcceptCommand(this));
        this.addCommand(new MilkywayDenyCommand(this));
        this.addCommand(new MilkywayCreateCommand(this));
    }
}
