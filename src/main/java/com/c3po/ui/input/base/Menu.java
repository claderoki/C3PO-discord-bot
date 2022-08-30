package com.c3po.ui.input.base;

import com.c3po.core.AccessControlList;
import com.c3po.core.AccessControlListMode;
import com.c3po.core.command.Context;
import com.c3po.helper.EmbedHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Setter
@Getter
public class Menu {
    protected final Map<String, MenuOption<?, ?, ?>> options = new LinkedHashMap<>();
    protected final Context context;
    protected final AccessControlList<Snowflake> acl = new AccessControlList<>();
    protected Integer maximumOptionsAllowed;
    protected AtomicInteger optionsHandled = new AtomicInteger(0);
    protected Consumer<EmbedCreateSpec.Builder> embedConsumer = null;
    protected Duration timeout = Duration.ofSeconds(360);

    public Menu(Context context) {
        this(context, false);
    }

    public Menu(Context context, boolean allowEveryone) {
        this.context = context;
        if (allowEveryone) {
            acl.setMode(AccessControlListMode.ALLOW_UNLESS_DENIED);
        } else {
            acl.allow(context.getEvent().getInteraction().getUser().getId());
        }
    }

    public void incrementOptionsHandled() {
        optionsHandled.getAndIncrement();
    }

    public int getOptionsHandled() {
        return optionsHandled.get();
    }

    public void addOption(MenuOption<?, ?, ?> option) {
        option.setContext(context);
        options.put(option.getCustomId(), option);
    }

    public EmbedCreateSpec getEmbed() {
        if (embedConsumer == null) {
            return null;
        }
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
        embed.color(EmbedHelper.COLOR);
        embedConsumer.accept(embed);
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
        return optionsHandled.get() < maximumOptionsAllowed;
    }

    public boolean isAllowed(ComponentInteractionEvent event) {
        return acl.isAllowed(event.getInteraction().getUser().getId());
    }
}
