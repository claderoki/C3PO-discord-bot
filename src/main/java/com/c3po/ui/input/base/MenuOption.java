package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import com.c3po.helper.RandomHelper;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import reactor.core.publisher.Mono;
import java.util.function.Function;

@Getter
@Setter
public abstract class MenuOption<T, F extends ComponentInteractionEvent, K extends ActionComponent> {
    protected final String name;
    private final String customId;

    private T value;
    protected Function<T, Void> setter;
    private boolean ownerOnly = false;
    protected @NonNull Context context;
    protected String emoji;

    public MenuOption(String name) {
        this.name = name;
        this.customId = generateCustomId();
    }

    public MenuOption(String name, T value) {
        this(name);
        this.value = value;
    }

    protected void setValue(T value) {
        if (setter != null) {
            setter.apply(value);
        }
        this.value = value;
    }

    public MenuOption<T, F, K> withEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    protected ReactionEmoji getEmoji() {
        if (emoji == null) {
            return null;
        }
        return ReactionEmoji.unicode(emoji);
    }

    private String generateCustomId() {
        return getClass().getSimpleName() + hashCode() + RandomHelper.generateString(5);
    }

    public abstract K getComponent();

    public abstract Mono<Void> execute(F event);

    public String getFullName() {
        return name;
    }

    public boolean shouldContinue() {
        return true;
    }

    protected boolean isBottomRow() {
        return !shouldContinue();
    }

    public boolean isAllowed(ComponentInteractionEvent event) {
        if (ownerOnly) {
            return event.getInteraction().getUser().getId().equals(context.getEvent().getInteraction().getUser().getId());
        } else {
            return true;
        }
    }
}
