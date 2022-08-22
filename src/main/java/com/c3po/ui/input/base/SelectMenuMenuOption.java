package com.c3po.ui.input.base;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.SelectMenu;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SelectMenuMenuOption<T> extends MenuOption<List<String>, SelectMenuInteractionEvent, SelectMenu> {
    private static final int MAX = 25;
    private Map<String, String> cachedOptions;
    private Map<String, T> optionMapping;
    @Getter
    private List<T> selected;

    final public SelectMenu getComponent() {
        return modifySelectMenu(SelectMenu.of(getCustomId(), getOptions()));
    }

    public SelectMenuMenuOption(String name) {
        super(name);
    }

    protected SelectMenu modifySelectMenu(SelectMenu selectMenu) {
        return selectMenu;
    }

    protected abstract List<T> fetchOptions();

    protected abstract String toValue(T value);

    protected abstract String toLabel(T value);

    private SelectMenu.Option createOption(Map.Entry<String, String> entry) {
        SelectMenu.Option option = SelectMenu.Option.of(
            entry.getValue(),
            entry.getKey()
        );
        List<String> values = getValue();
        if (values != null) {
            option.withDefault(getValue().contains(entry.getKey()));
        }
        return option;
    }

    private boolean shouldRefreshCache() {
        return cachedOptions == null;
    }

    protected List<SelectMenu.Option> getOptions() {
        if (shouldRefreshCache()) {
            List<T> options = fetchOptions();
            cachedOptions = options.stream().collect(Collectors.toMap(this::toValue, this::toLabel));
            optionMapping = options.stream().collect(Collectors.toMap(this::toValue, c -> c));
        }

        var options = cachedOptions.entrySet()
            .stream()
            .map(this::createOption)
            .toList();
        return options.size() > MAX ? options.subList(0, MAX) : options;
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        List<String> oldValues = getValue();
        setValue(event.getValues());
        if (!Objects.equals(oldValues, getValue())) {
            selected = getValue().stream().map(c -> optionMapping.get(c)).toList();
        }
        return Mono.empty();
    }

}
