package com.c3po.command.snakeoil;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class SnakeOilCommandGroup extends CommandGroup {
    public SnakeOilCommandGroup() {
        super(CommandCategory.SNAKE_OIL,"snakeoil","Snake oil game");
        addCommand(new SnakeOilStartCommand(this));
    }
}
