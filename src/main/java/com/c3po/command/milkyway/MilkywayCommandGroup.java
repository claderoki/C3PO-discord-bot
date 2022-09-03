package com.c3po.command.milkyway;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MilkywayCommandGroup extends CommandGroup {
    @Autowired
    public MilkywayCommandGroup(MilkywayAcceptCommand accept, MilkywayDenyCommand deny, MilkywayCreateCommand create, MilkywayExtendCommand extend) {
        super(CommandCategory.MILKYWAY,"Milkies");
        this.addCommands(accept, deny, create, extend);
    }
}
