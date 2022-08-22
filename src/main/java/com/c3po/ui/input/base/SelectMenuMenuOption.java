package com.c3po.ui.input.base;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class SelectMenuMenuOption extends MenuOption<List<String>, SelectMenuInteractionEvent, SelectMenu> {
    Map<String, String> cachedOptions = null;

    protected List<String> labels;

    public SelectMenu getComponent() {
        return SelectMenu.of(getCustomId(), getOptions());
    }

    public SelectMenuMenuOption(String name) {
        super(name);
    }

    protected abstract Map<String, String> getOptionCache();

    private SelectMenu.Option createOption(Map.Entry<String, String> entry) {
        SelectMenu.Option option = SelectMenu.Option.of(
            entry.getValue(),
            entry.getKey()
        );
        List<String> values = getValue();
        if (values != null) {
            option.withDefault(getValue().contains(entry.getKey()));
        }
        modifyOption(option);
        return option;
    }

    protected void modifyOption(SelectMenu.Option option) {

    }

    protected List<SelectMenu.Option> getOptions() {
        if (cachedOptions == null) {
            cachedOptions = getOptionCache();
        }

        var options = cachedOptions.entrySet()
            .stream()
            .map(this::createOption)
            .toList();

        return options.size() > 25 ? options.subList(0, 25) : options;
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        List<String> oldValues = getValue();
        setValue(event.getValues());
        if (!Objects.equals(oldValues, getValue())) {
            labels = getValue().stream().map(c -> cachedOptions.get(c)).toList();
        }
        return Mono.empty();
    }

}
