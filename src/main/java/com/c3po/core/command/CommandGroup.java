package com.c3po.core.command;

import com.c3po.helper.DiscordCommandOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class CommandGroup extends Command {
    private final List<SubCommand> commands = new ArrayList<>();

    protected CommandGroup(CommandCategory category, String name, String description, DiscordCommandOptionType type) {
        super(category, name, description, type);
    }

    public void addCommand(SubCommand command) {
        commands.add(command);
    }

    @Override
    public List<ApplicationCommandOptionData> getOptions() {
        final List<ApplicationCommandOptionData> options = new ArrayList<>();
        for (final Command cmd : commands) {
            options.add(ApplicationCommandOptionData.builder()
                .name(cmd.getName())
                .description(cmd.getDescription())
                .type(cmd.getType().getValue())
                .options(cmd.getOptions())
                .build());
        }
        return Collections.unmodifiableList(options);
    }
}
