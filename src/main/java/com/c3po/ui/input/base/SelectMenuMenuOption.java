package com.c3po.ui.input.base;

import com.c3po.core.DataFormatter;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class SelectMenuMenuOption extends MenuOption<String, SelectMenuInteractionEvent, SelectMenu> {
    Map<String, String> cachedOptions = null;

    protected String label;

    @Override
    protected String getPrettyValue() {
        return DataFormatter.prettify(label);
    }

    public SelectMenu getComponent() {
        return SelectMenu.of(getCustomId(), getOptions());
    }

    public SelectMenuMenuOption(String name) {
        super(name);
    }

    public SelectMenuMenuOption(String name, String value) {
        super(name, value);
    }

    protected abstract Map<String, String> getOptionCache();

    protected List<SelectMenu.Option> getOptions() {
        if (cachedOptions == null) {
            cachedOptions = getOptionCache();
        }

        var options = cachedOptions.entrySet().stream().map(c -> SelectMenu.Option.of(c.getValue(), c.getKey()).withDefault(c.getKey().equals(value)))
            .toList();

        return options.size() > 25 ? options.subList(0, 25) : options;
    }

    @Override
    public Mono<?> execute(SelectMenuInteractionEvent event) {
        String oldValue = value;
        value = event.getValues().get(0);
        if (!Objects.equals(oldValue, value)) {
            label = cachedOptions.get(value);
        }

        return Mono.empty();
    }

}
