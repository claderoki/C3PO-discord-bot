package com.c3po.ui.input.base;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;

public abstract class ButtonMenuOption<T> extends MenuOption<T, ButtonInteractionEvent, Button> {
    public ButtonMenuOption(String name) {
        super(name);
    }

    public ButtonMenuOption(String name, T value) {
        super(name, value);
    }

    public Button.Style getButtonStyle() {
        if (value == null) {
            return Button.Style.SECONDARY;
        } else {
            return Button.Style.SUCCESS;
        }
    }

    public Button getComponent() {
        return switch (getButtonStyle()) {
            case PRIMARY -> Button.primary(getCustomId(), getEmoji(), getFullName());
            case SECONDARY -> Button.secondary(getCustomId(), getEmoji(), getFullName());
            case SUCCESS -> Button.success(getCustomId(), getEmoji(), getFullName());
            case DANGER -> Button.danger(getCustomId(), getEmoji(), getFullName());
            case LINK -> Button.link(getCustomId(), getEmoji(), getFullName());
            default -> throw new IllegalStateException("Unexpected value: " + getButtonStyle());
        };
    }
}