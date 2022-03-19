package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class VoidMenuOption extends ButtonMenuOption<Void> {
    public VoidMenuOption(String name, Void value) {
        super(name, value);
    }

    public VoidMenuOption(String name) {
        super(name);
    }

    public Button getComponent() {
        return Button.secondary(getCustomId(), getFullName());
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        return event.acknowledge();
    }

}
