package com.c3po.ui.input.base;

import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.SelectMenu;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SelectMenuMenuOption<T> extends MenuOption<List<T>, SelectMenuInteractionEvent, SelectMenu> {
    private static final int MAX = 25;
    private Map<String, T> optionMapping;

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

    protected abstract String toValue(T option);

    protected abstract String toLabel(T option);

    private SelectMenu.Option createOption(T option) {
        SelectMenu.Option selectOption = SelectMenu.Option.of(toLabel(option), toValue(option));
        Optional.ofNullable(getValue()).map(v -> v.contains(option)).ifPresent(v -> selectOption.withDefault(true));
        return selectOption;
    }

    private boolean shouldRefreshCache() {
        return optionMapping == null;
    }

    private void cacheOptions() {
        optionMapping = fetchOptions()
            .stream()
            .collect(Collectors.toMap(this::toValue, Function.identity()));
    }

    private List<SelectMenu.Option> getCachedOptions() {
        var options = optionMapping.values()
            .stream()
            .map(this::createOption)
            .toList();
        return options.size() > MAX ? options.subList(0, MAX) : options;
    }

    protected List<SelectMenu.Option> getOptions() {
        if (shouldRefreshCache()) {
            cacheOptions();
        }
        return getCachedOptions();
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        setValue(event.getValues().stream().map(v -> optionMapping.get(v)).toList());
        return Mono.empty();
    }
}
