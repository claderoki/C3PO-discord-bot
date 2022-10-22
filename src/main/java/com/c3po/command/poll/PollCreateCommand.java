package com.c3po.command.poll;

import com.c3po.command.poll.menu.Poll;
import com.c3po.command.poll.menu.PollCreateMenu;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.ui.input.*;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PollCreateCommand extends PollSubCommand {
    protected PollCreateCommand() {
        super("create", "Create a poll.");
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Poll.PollBuilder pollBuilder = Poll.builder();
        PollCreateMenu menu = new PollCreateMenu(context, pollBuilder);
        return new MenuManager<>(menu).waitFor().then(Mono.fromRunnable(() -> {
            var a = "";
        }));
    }
}
