package com.c3po.core.command;

import com.c3po.helper.DiscordCommandOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class CommandGroup extends Command {
    private final List<SubCommand> commands = new ArrayList<>();

    protected CommandGroup(CommandCategory category, String name, String description) {
        super(category, name, description, DiscordCommandOptionType.SUB_COMMAND_GROUP);
    }
    protected CommandGroup(CommandCategory category, String description) {
        this(category, category.getValue(), description);
    }

    public void addCommand(SubCommand command) {
        commands.add(command);
    }
    public void addCommands(SubCommand... commands) {
        for(SubCommand command: commands) {
            addCommand(command);
        }
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

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        throw new IllegalStateException("Shouldn't be called directly.");
    }
}
