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

//        Menu menu = new Menu(context);
//        menu.addOption(new BooleanMenuOption("Anonymous").withEmoji("\uD83E\uDD77"));
//        menu.addOption(new BooleanMenuOption("Mention role").withEmoji("™️"));
//        menu.addOption(new BooleanMenuOption("Pin").withEmoji("\uD83D\uDCCC"));
//        menu.addOption(new BooleanMenuOption("Delete after results").withEmoji("\uD83D\uDDD1"));
//        menu.addOption(new SubMenuOption("Poll channel", new SubMenu(context, new ChannelMenuOption("Channels"))));
//        menu.addOption(new SubMenuOption("Result channel", new SubMenu(context, new ChannelMenuOption("Channels"))));
//        menu.addOption(new LongMenuOption("Max votes per user"));
//        return MenuManager.waitForMenu(menu, "OK");
    }
}
