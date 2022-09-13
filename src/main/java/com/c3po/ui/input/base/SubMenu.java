package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import discord4j.core.object.component.Button;
import lombok.Getter;

@Getter
public class SubMenu extends Menu {
    private final MenuOption<?, ?, ?> defaultOption;

    public SubMenu(Context context, MenuOption<?, ?, ?> defaultOption) {
        this(context, defaultOption, false);
    }

    public SubMenu(Context context, MenuOption<?, ?, ?> defaultOption, boolean allowEveryone) {
        super(context, allowEveryone);
        this.defaultOption = defaultOption;
        this.addOption(defaultOption);
    }

}
