package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Setter
@Getter
public class Menu {
    protected final Map<String, MenuOption<?, ?, ?>> options = new HashMap<>();
    protected final Context context;
    protected Integer maximumOptionsAllowed;
    protected boolean ownerOnly = true;
    protected int optionsHandled;
    protected Consumer<EmbedCreateSpec.Builder> embedConsumer = null;
    protected Duration timeout = Duration.ofSeconds(5);

    public void incrementOptionsHandled() {
        optionsHandled++;
    }

    public void addOption(MenuOption<?, ?, ?> option) {
        option.setContext(context);
        options.put(option.getCustomId(), option);
    }

    public EmbedCreateSpec getEmbed() {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
        embed.color(EmbedHelper.COLOR);
        if (embedConsumer != null) {
            embedConsumer.accept(embed);
        }
        return embed.build();
    }

    public MenuOption<?, ?, ?> matchOption(String customId) {
        return options.get(customId);
    }

    final public List<LayoutComponent> getComponents() {
        List<LayoutComponent> components = new ArrayList<>();

        List<Button> buttons = new ArrayList<>();
        List<Button> lastRowButtons = new ArrayList<>();

        for (MenuOption<?, ?, ?> option: options.values()) {
            ActionComponent component = option.getComponent();
            if (component instanceof Button button) {
                if (!option.isBottomRow()) {
                    buttons.add(button);
                } else {
                    lastRowButtons.add(button);
                }
            } else {
                components.add(ActionRow.of(component));
            }
            if (buttons.size() == 5) {
                components.add(ActionRow.of(buttons));
                buttons = new ArrayList<>();
            }
        }

        if (!buttons.isEmpty()) {
            components.add(ActionRow.of(buttons));
        }

        if (!lastRowButtons.isEmpty()) {
            components.add(ActionRow.of(lastRowButtons));
        }

        return components;
    }

    public boolean shouldContinue() {
        if (maximumOptionsAllowed == null) {
            return true;
        }
        return maximumOptionsAllowed > (optionsHandled+1);
    }

    public boolean isAllowed(ComponentInteractionEvent event) {
        if (ownerOnly) {
            return event.getInteraction().getUser().getId().equals(context.getEvent().getInteraction().getUser().getId());
        } else {
            return true;
        }
    }
}
