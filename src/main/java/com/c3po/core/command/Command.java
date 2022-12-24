package com.c3po.core.command;

import com.c3po.core.Scope;
import com.c3po.helper.DiscordCommandOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public abstract class Command {
    private final CommandCategory category;
    private final String name;
    private final String description;
    private final List<ApplicationCommandOptionData> options;
    private final DiscordCommandOptionType type;

    protected Command(CommandCategory category, String name, String description, DiscordCommandOptionType type) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.type = type;
        this.options = new ArrayList<>();
    }

    public Optional<Bucket> getBucket() {
        return Optional.of(new Bucket(Scope.USER, 1));
    }

    public CommandSettings getSettings() {
        return null;
    }

    protected abstract Mono<Void> execute(Context context) throws RuntimeException;

    protected Mono<Void> beforeExecute(Context context) {
        return Mono.empty();
    }

    protected Mono<Void> afterExecute(Context context) {
        return Mono.empty();
    }

    public final Mono<Void> run(Context context) {
        return Mono.empty()
            .then(beforeExecute(context))
            .then(execute(context))
            .then(afterExecute(context))
        ;
    }

    public final void addOption(Consumer<ImmutableApplicationCommandOptionData.Builder> option) {
        final ImmutableApplicationCommandOptionData.Builder mutatedOption = ApplicationCommandOptionData.builder();
        option.accept(mutatedOption);
        this.options.add(mutatedOption.build());
    }

    public final ApplicationCommandRequest asRequest() {
        return ApplicationCommandRequest.builder()
            .name(getName())
            .description(getDescription())
            .addAllOptions(getOptions())
            .build();
    }

    public String hash() {
        return String.valueOf(Objects.hash(name, options.stream().map(ApplicationCommandOptionData::name).collect(Collectors.joining(", "))));
    }

}
