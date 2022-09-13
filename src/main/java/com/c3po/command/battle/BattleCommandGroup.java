package com.c3po.command.battle;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BattleCommandGroup extends CommandGroup {
    @Autowired
    public BattleCommandGroup(BattleStartCommand start) {
        super(CommandCategory.BATTLE,"Battle game");
        addCommands(start);
    }
}
