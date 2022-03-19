package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import discord4j.core.object.component.Button;
import lombok.Getter;

@Getter
public class SubMenu extends Menu {
    private final MenuOption defaultOption;

    public SubMenu(Context context, MenuOption defaultOption) {
        super(context);
        this.defaultOption = defaultOption;
        this.addOption(defaultOption);
    }

    private boolean hasBackButton() {
        return true;
    }

    public String getBackButtonCustomId() {
        return this.getClass().getSimpleName() + "backButton";
    }

    private Button getBackButton() {
        return Button.secondary(getBackButtonCustomId(), "Back");
    }
}
