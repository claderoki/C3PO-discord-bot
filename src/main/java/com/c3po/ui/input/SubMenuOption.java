package com.c3po.ui.input;

import com.c3po.core.DataFormatter;
import com.c3po.ui.input.base.ButtonMenuOption;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.SubMenu;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;


public class SubMenuOption extends ButtonMenuOption<Long> {
    private final SubMenu subMenu;

    public SubMenuOption(String name, Long value, SubMenu subMenu) {
        super(name, value);
        this.subMenu = subMenu;
    }

    public SubMenuOption(String name, SubMenu subMenu) {
        super(name);
        this.subMenu = subMenu;
    }

    @Override
    protected String getPrettyValue() {
        return DataFormatter.prettify(subMenu.getDefaultOption().getValue());
    }

    private boolean hasBackButton() {
        return true;
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        if (hasBackButton()) {
            subMenu.addOption(new BackButtonMenuOption());
        }

        return MenuManager.waitForMenu(subMenu, "Hmm");
    }
}
