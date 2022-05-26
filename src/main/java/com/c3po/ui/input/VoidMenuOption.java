package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class VoidMenuOption extends ButtonMenuOption<Void> {
    @Setter
    private boolean shouldContinue = true;

    public VoidMenuOption(String name) {
        super(name);
    }

    @Setter
    private Function<ButtonInteractionEvent, Mono<?>> executor;

    @Override
    public Button.Style getButtonStyle() {
        return Button.Style.SECONDARY;
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        if (executor == null) {
            return event.deferEdit();
        }
        return event.deferEdit().then(executor.apply(event));
    }

    protected boolean shouldContinue() {
        return shouldContinue;
    }

}
