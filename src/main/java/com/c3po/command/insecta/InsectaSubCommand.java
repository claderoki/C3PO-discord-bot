package com.c3po.command.insecta;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import org.springframework.stereotype.Component;

@Component
public abstract class InsectaSubCommand extends SubCommand {
    protected InsectaSubCommand(String name, String description) {
        super(CommandCategory.INSECTA, name, description);
    }
    protected InsectaSubCommand(String name) {
        super(CommandCategory.INSECTA, name);
    }
}
