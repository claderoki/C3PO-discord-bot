package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.Replier;
import com.c3po.ui.input.base.SubMenu;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.Setter;
import reactor.core.publisher.Mono;

public class SubMenuOption extends ButtonMenuOption<Void> {
    private final SubMenu subMenu;
    private final boolean edit;
    @Setter
    private boolean ephemeral;

    public SubMenuOption(String name, SubMenu subMenu, boolean edit) {
        super(name);
        this.subMenu = subMenu;
        this.edit = edit;
    }

    private boolean hasBackButton() {
        return true;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
//        if (hasBackButton()) {
//            subMenu.addOption(new BackButtonMenuOption());
//        }
        Replier replier;
        if (!edit) {
            replier = new Replier(subMenu.getContext().getEvent());
        } else {
            replier = new Replier(event);
        }
        replier.setEphemeral(ephemeral);
        return new MenuManager<>(subMenu, replier).waitFor().then();
    }
}
