package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class SingleUseButtonMenuOption extends ButtonMenuOption<Void> {
    private boolean clicked = false;

    public SingleUseButtonMenuOption(String name) {
        super(name);
    }

    @Setter
    private Function<ButtonInteractionEvent, Mono<Void>> executor;

    @Override
    public Button.Style getButtonStyle() {
        return Button.Style.SECONDARY;
    }

    @Override
    public Button modifyButton(Button button) {
        return button.disabled(clicked);
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        clicked = true;
        if (executor == null) {
            return event.deferEdit();
        }
        return event.deferEdit().then(executor.apply(event));
    }

}
