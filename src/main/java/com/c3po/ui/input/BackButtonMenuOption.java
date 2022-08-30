package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public class BackButtonMenuOption extends ButtonMenuOption<Void> {
    public BackButtonMenuOption(String name) {
        super(name);
    }

    public BackButtonMenuOption() {
        super("Back");
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        return event.deferEdit();
    }

    public boolean shouldContinue() {
        return false;
    }

}
