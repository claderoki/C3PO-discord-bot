package com.c3po.command.profile;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import reactor.core.publisher.Mono;

public class ProfileCommandGroup extends CommandGroup {
    public ProfileCommandGroup() {
        super(CommandCategory.PROFILE,"profile","Profile info");
        addCommand(new ProfileViewCommand(this));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        throw new IllegalStateException("Shouldn't be called directly.");
    }
}
