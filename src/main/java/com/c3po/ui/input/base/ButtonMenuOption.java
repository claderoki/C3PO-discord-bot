package com.c3po.ui.input.base;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Setter;

import java.util.function.Predicate;

public abstract class ButtonMenuOption<T> extends MenuOption<T, ButtonInteractionEvent, Button> {
    public ButtonMenuOption(String name) {
        super(name);
    }

    @Setter
    private Predicate<Void> disabledIf;

    public ButtonMenuOption(String name, T value) {
        super(name, value);
    }

    public Button.Style getButtonStyle() {
        if (getValue() == null) {
            return Button.Style.SECONDARY;
        } else {
            return Button.Style.SUCCESS;
        }
    }

    public Button modifyButton(Button button) {
        if (disabledIf != null && disabledIf.test(null)) {
            return button.disabled(true);
        }
        return button;
    }

    final public Button getComponent() {
        Button button = switch (getButtonStyle()) {
            case PRIMARY -> Button.primary(getCustomId(), getEmoji(), getFullName());
            case SECONDARY -> Button.secondary(getCustomId(), getEmoji(), getFullName());
            case SUCCESS -> Button.success(getCustomId(), getEmoji(), getFullName());
            case DANGER -> Button.danger(getCustomId(), getEmoji(), getFullName());
            case LINK -> Button.link(getCustomId(), getEmoji(), getFullName());
            default -> throw new IllegalStateException("Unexpected value: " + getButtonStyle());
        };
        return modifyButton(button);
    }
}
