package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.Interactor;
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

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        Interactor interactor;
        if (!edit) {
            interactor = new Interactor(subMenu.getContext().getEvent());
        } else {
            interactor = new Interactor(event);
        }
        interactor.setEphemeral(ephemeral);
        return new MenuManager<>(subMenu, interactor).waitFor().then();
    }
}
