package com.c3po.command.pigeon;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.service.PigeonService;
import reactor.core.publisher.Mono;

public abstract class PigeonSubCommand extends SubCommand {
    protected final PigeonService pigeonService;
    protected final PigeonRepository pigeonRepository;

    protected PigeonSubCommand(CommandGroup group, String name, String description) {
        super(group, name, description);
        pigeonService = new PigeonService();
        pigeonRepository = PigeonRepository.db();
    }

    protected PigeonValidation getValidation() {
        return PigeonValidation.builder()
            .needsActivePigeon(true)
            .build();
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        return _execute(context);
    }

    public Mono<?> _execute(Context context) {
        return Mono.empty();
    }

}
