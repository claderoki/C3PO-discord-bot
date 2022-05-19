package com.c3po.command.milkyway;

import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.SubCommand;
import com.c3po.service.MilkywayService;

public abstract class MilkywaySubCommand extends SubCommand {
    protected final MilkywayService milkywayService = new MilkywayService();
    protected final MilkywayRepository milkywayRepository = MilkywayRepository.db();

    protected MilkywaySubCommand(CommandGroup group, String name, String description) {
        super(group, name, description);
    }
}
