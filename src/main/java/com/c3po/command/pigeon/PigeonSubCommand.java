package com.c3po.command.pigeon;

import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import reactor.core.publisher.Mono;

public abstract class PigeonSubCommand extends SubCommand {
    protected PigeonSubCommand(CommandGroup group, String name, String description) {
        super(group, name, description);
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
