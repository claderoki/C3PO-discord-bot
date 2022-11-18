package com.c3po.activitytracker;

import com.c3po.core.command.Context;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ActivityTrackerManageCommand extends ActivityTrackerSubCommand {
    protected ActivityTrackerManageCommand() {
        super("manage", "Manage activity in this server.");
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        return Mono.empty();
    }
}
