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

    @Setter
    private Function<ButtonInteractionEvent, Mono<Void>> executor;

    public VoidMenuOption(String name) {
        super(name);
    }

    @Override
    public Button.Style getButtonStyle() {
        return Button.Style.SECONDARY;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        if (executor == null) {
            return event.deferEdit();
        }
        return event.deferEdit().then(Mono.defer(() -> executor.apply(event)));
    }

    public boolean shouldContinue() {
        return shouldContinue;
    }

}
