package com.c3po.command.hangman;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;

public class HangmanCommandGroup extends CommandGroup {
    public HangmanCommandGroup() {
        super(CommandCategory.HANGMAN, "hangman", "no description");
        addCommand(new HangmanStartCommand(this));
    }
}
