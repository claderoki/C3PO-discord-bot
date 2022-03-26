package com.c3po.ui.input.base;

import com.c3po.core.DataFormatter;
import com.c3po.core.command.Context;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.function.Function;

public abstract class MenuOption<T, F extends ComponentInteractionEvent, K extends ActionComponent> {
    protected final String name;

    @Getter
    private T value;

    @Nullable
    @Setter
    protected Function<T, Void> setter;

    protected void setValue(T value) {
        if (setter != null) {
            setter.apply(value);
        }
        this.value = value;
    }

    @Setter
    @NonNull
    protected Context context;

    protected String emoji;

    public MenuOption withEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public MenuOption(String name) {
        this.name = name;
    }

    public MenuOption(String name, T value) {
        this.name = name;
        this.value = value;
    }

    protected ReactionEmoji getEmoji() {
        return emoji != null ? ReactionEmoji.unicode(emoji) : null;
    }

    protected String getCustomId() {
        return this.getClass().getSimpleName() + name;
    }

    public abstract K getComponent();

    public abstract Mono<?> execute(F event);

    protected String getPrettyValue() {
        return DataFormatter.prettify(value);
    }

    public String getFullName() {
        return name;
    }

    protected boolean shouldContinue() {
        return true;
    }

}
