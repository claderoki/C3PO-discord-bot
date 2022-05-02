package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import com.c3po.helper.LogHelper;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Menu {
    private final Map<String, MenuOption> options = new HashMap<>();
    @Getter
    private final Context context;
    @Getter
    @Setter
    private Integer maximumOptionsAllowed;

    @Getter
    private int optionsHandled;

    public void incrementOptionsHandled() {
        optionsHandled++;
    }

    public void addOption(MenuOption option) {
        option.setContext(context);
        options.put(option.getCustomId(), option);
    }

    public MenuOption matchOption(String customId) {
        return options.get(customId);
    }

    public List<LayoutComponent> getComponents() {
        List<LayoutComponent> components = new ArrayList<>();

        List<Button> buttons = new ArrayList<>();
        List<Button> lastRowButtons = new ArrayList<>();

        for (MenuOption option: options.values()) {
            ActionComponent component = option.getComponent();
            if (component instanceof Button button) {
                if (option.shouldContinue()) {
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
}
