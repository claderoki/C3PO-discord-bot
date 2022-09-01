package com.c3po.command.hangman;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.stereotype.Component;

@Component
public class HangmanCommandGroup extends CommandGroup {
    public HangmanCommandGroup(HangmanStartCommand start) {
        super(CommandCategory.HANGMAN, "hangman", "no description");
        addCommands(start);
    }
}
