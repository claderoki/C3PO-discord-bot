package com.c3po.command.snakeoil;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class SnakeOilCommandGroup extends CommandGroup {
    public SnakeOilCommandGroup(SnakeOilStartCommand start) {
        super(CommandCategory.SNAKE_OIL,"Snake oil game");
        addCommands(start);
    }
}
