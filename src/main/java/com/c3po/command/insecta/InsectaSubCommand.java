package com.c3po.command.insecta;

import com.c3po.command.insecta.service.InsectaService;
import com.c3po.connection.repository.InsectaRepository;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class InsectaSubCommand extends SubCommand {
    @Autowired
    protected InsectaRepository insectaRepository;
    @Autowired
    protected InsectaService insectaService;

    protected InsectaSubCommand(String name, String description) {
        super(CommandCategory.INSECTA, name, description);
    }
    protected InsectaSubCommand(String name) {
        super(CommandCategory.INSECTA, name);
    }
}
