package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class BackButtonMenuOption extends ButtonMenuOption<Void> {
    public BackButtonMenuOption(String name) {
        super(name);
    }

    public BackButtonMenuOption() {
        super("Back");
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        return event.deferEdit();
    }

    protected boolean shouldContinue() {
        return false;
    }

}
