package com.c3po.command.poll;

import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.ui.input.*;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import reactor.core.publisher.Mono;

public class PollCreateCommand extends SubCommand {
    protected PollCreateCommand(PollCommandGroup group) {
        super(group, "create", "Create a poll.");
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {

        Menu menu = new Menu(context);
        menu.setEmbedConsumer(c -> c.description("Your pigeon has done nothing more!"));
        VoidMenuOption option = new VoidMenuOption("Don't remind me!");
        option.setShouldContinue(false);
        option.withEmoji("❗");
        option.setExecutor((e) ->
            e.createFollowup().withContent("Okay, I wont remind you, or do anything at all except for send this message.").then());
        menu.addOption(option);

        return new MenuManager(menu).waitFor().then();
    }
}
