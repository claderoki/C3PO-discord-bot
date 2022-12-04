package com.c3po.ui.input;

import com.c3po.ui.input.base.ButtonMenuOption;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Setter;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Accessors(chain = true)
public class DynamicButtonMenuOption extends ButtonMenuOption<Void> {
    @Setter
    private Button.Style buttonStyle = Button.Style.SECONDARY;

    public Button.Style getButtonStyle() {
        return buttonStyle;
    }

    public DynamicButtonMenuOption(String name) {
        super(name);
    }

    private Function<ButtonInteractionEvent, Mono<Void>> onClickEvent;

    public DynamicButtonMenuOption onClick(Function<ButtonInteractionEvent, Mono<Void>> function) {
        onClickEvent = function;
        return this;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        if (onClickEvent == null) {
            return event.deferEdit();
        }
        return event.deferEdit().then(onClickEvent.apply(event));
    }

}
