package com.c3po.command.battle;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class BattleCommandGroup extends CommandGroup {
    public BattleCommandGroup(BattleStartCommand start) {
        super(CommandCategory.BATTLE,"Battle game");
        addCommands(start);
    }
}
