package com.c3po.command.insecta;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class InsectaCommandGroup extends CommandGroup {
    public InsectaCommandGroup(InsectaBuyCommand buy, InsectaCollectCommand collect) {
        super(CommandCategory.INSECTA, "no description");
        addCommands(buy, collect);
    }
}
