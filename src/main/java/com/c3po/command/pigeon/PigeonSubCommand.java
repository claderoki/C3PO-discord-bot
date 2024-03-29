package com.c3po.command.pigeon;

import com.c3po.command.pigeon.validation.PigeonValidation;
import com.c3po.command.pigeon.validation.PigeonValidationSettings;
import com.c3po.connection.repository.PigeonRepository;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.service.PigeonService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public abstract class PigeonSubCommand extends SubCommand {
    @Autowired
    protected PigeonService pigeonService;
    @Autowired
    protected PigeonRepository pigeonRepository;
    @Autowired
    protected PigeonValidation validation;

    protected PigeonSubCommand(String name, String description) {
        super(CommandCategory.PIGEON, name, description);
    }

    protected PigeonValidationSettings getValidationSettings() {
        return PigeonValidationSettings.builder()
            .needsActivePigeon(true)
            .build();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        return _execute(context);
    }

    public Mono<Void> _execute(Context context) {
        return Mono.empty();
    }

}
