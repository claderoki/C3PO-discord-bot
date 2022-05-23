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
import java.util.Optional;
import java.util.function.Consumer;

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

    public abstract Mono<?> execute(Context context) throws RuntimeException;

    protected Mono<List<CommandValidation<?>>> getValidations() {
        return Mono.just(List.of());
    }

    protected Mono<?> beforeExecute(Context context) {
        return Mono.empty();
    }

    protected Mono<?> afterExecute(Context context) {
        return Mono.empty();
    }

    public Mono<?> run(Context context) {
        return getValidations().map(validations -> validations
                .stream().map(validation -> validation.validate(context)))
            .then(beforeExecute(context))
            .then(execute(context))
            .then(afterExecute(context))
        ;
    }

    public void addOption(Consumer<ImmutableApplicationCommandOptionData.Builder> option) {
        final ImmutableApplicationCommandOptionData.Builder mutatedOption = ApplicationCommandOptionData.builder();
        option.accept(mutatedOption);
        this.options.add(mutatedOption.build());
    }

    public ApplicationCommandRequest asRequest() {
        return ApplicationCommandRequest.builder()
            .name(getName())
            .description(getDescription())
            .addAllOptions(getOptions())
            .build();
    }

}
