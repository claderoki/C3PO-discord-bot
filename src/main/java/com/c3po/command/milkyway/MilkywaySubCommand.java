package com.c3po.command.milkyway;

import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import com.c3po.service.MilkywayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public abstract class MilkywaySubCommand extends SubCommand {
    @Autowired
    protected MilkywayService milkywayService;

    @Autowired
    protected MilkywayRepository milkywayRepository;

    @Autowired
    protected AutowireCapableBeanFactory beanFactory;

    protected MilkywaySubCommand(String name, String description) {
        super(CommandCategory.MILKYWAY, name, description);
    }
}
