package com.c3po.ui;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public abstract class BaseButton {

    public Button getButton() {
        return Button.secondary(getCode(), getLabel());
    }

    public abstract String getCode();

    public abstract String getLabel();

    public abstract Mono<Void> handle(ButtonInteractionEvent event) throws Exception;

}
