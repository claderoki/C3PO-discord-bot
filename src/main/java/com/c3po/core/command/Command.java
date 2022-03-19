package com.c3po.core.command;

import com.c3po.helper.DiscordCommandOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
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

    public CommandSettings getSettings() {
        return null;
    }

    public abstract Mono<?> execute(Context context) throws RuntimeException;

    public void addOption(Consumer<ImmutableApplicationCommandOptionData.Builder> option) {
        final ImmutableApplicationCommandOptionData.Builder mutatedOption = ApplicationCommandOptionData.builder();
        option.accept(mutatedOption);
        this.options.add(mutatedOption.build());
    }

    public ApplicationCommandRequest asRequest() {
        return ApplicationCommandRequest.builder()
            .name(this.getName())
            .description(this.getDescription())
            .addAllOptions(this.getOptions())
            .build();
    }

}