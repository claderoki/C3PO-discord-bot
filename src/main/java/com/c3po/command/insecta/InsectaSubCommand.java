package com.c3po.command.insecta;

import com.c3po.command.insecta.service.InsectaService;
import com.c3po.connection.repository.InsectaRepository;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.SubCommand;
import lombok.RequiredArgsConstructor;

public abstract class InsectaSubCommand extends SubCommand {
    protected final InsectaRepository insectaRepository;
    protected final InsectaService insectaService;

    protected InsectaSubCommand(String name, String description, InsectaRepository insectaRepository, InsectaService insectaService) {
        super(CommandCategory.INSECTA, name, description);
        this.insectaRepository = insectaRepository;
        this.insectaService = insectaService;
    }
    protected InsectaSubCommand(String name, InsectaRepository insectaRepository, InsectaService insectaService) {
        super(CommandCategory.INSECTA, name);
        this.insectaRepository = insectaRepository;
        this.insectaService = insectaService;
    }
}
