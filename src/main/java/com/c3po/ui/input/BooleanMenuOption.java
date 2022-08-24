package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class BooleanMenuOption extends ButtonMenuOption<Boolean> {
    public BooleanMenuOption(String name, Boolean value) {
        super(name, value);
    }

    public BooleanMenuOption(String name) {
        super(name);
    }

    public Button.Style getButtonStyle() {
        if (getValue() == null || !getValue()) {
            return Button.Style.SECONDARY;
        } else {
            return Button.Style.SUCCESS;
        }
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        setValue(getValue() == null || !getValue());
        return event.deferEdit();
    }

}
