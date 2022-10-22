package com.c3po.command.poll.menu;

import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.Emoji;
import com.c3po.helper.waiter.DurationParser;
import com.c3po.ui.input.BooleanMenuOption;
import com.c3po.ui.input.WaiterMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuOption;

import java.util.List;

public class PollCreateMenu extends Menu {
    private final Poll.PollBuilder poll;

    public PollCreateMenu(Context context, Poll.PollBuilder poll) {
        super(context);
        this.poll = poll;
        setEmbedConsumer(e -> e.description("Creating a poll"));
        this.addOptions();
    }

    private List<MenuOption<?,?,?>> getAvailableOptions() {
        return List.of(
            new BooleanMenuOption("Pin?", false)
                .setSetter(poll::pin)
                .setEmoji(Emoji.PIN),
            new BooleanMenuOption("Delete after results?", false)
                .setSetter(poll::deleteAfterResults)
                .setEmoji(Emoji.TRASH),
            new WaiterMenuOption<>("Due date", DurationParser.builder().build())
                .setSetter(v -> poll.dueDate(DateTimeHelper.now().plus(v)))
                .setEmoji(Emoji.TRASH)
        );
    }

    private void addOptions() {
        getAvailableOptions().forEach(this::addOption);
    }
}
