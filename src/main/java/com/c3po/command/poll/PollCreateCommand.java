package com.c3po.command.poll;

import com.c3po.core.command.CommandManager;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.ui.input.BooleanMenuOption;
import com.c3po.ui.input.ChannelMenuOption;
import com.c3po.ui.input.LongMenuOption;
import com.c3po.ui.input.SubMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.SubMenu;
import reactor.core.publisher.Mono;

public class PollCreateCommand extends SubCommand {
    protected PollCreateCommand(PollCommandGroup group) {
        super(group, "create", "Create a poll.");
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {

        return Mono.empty();

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
