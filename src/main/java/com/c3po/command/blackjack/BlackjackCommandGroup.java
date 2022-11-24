package com.c3po.command.blackjack;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class BlackjackCommandGroup extends CommandGroup {
    public BlackjackCommandGroup(BlackjackStartCommand start) {
        super(CommandCategory.BLACKJACK,"Blackjack game");
        addCommands(start);
    }
}
