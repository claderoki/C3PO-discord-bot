package com.c3po.command;

import com.c3po.core.command.Command;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.helper.DiscordCommandOptionType;
import discord4j.common.util.Snowflake;
import reactor.core.publisher.Mono;

public class SendCommand extends Command {
    protected SendCommand(CommandCategory category, String name, String description, DiscordCommandOptionType type) {
        super(category, name, description, type);
    }

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        Snowflake channelId = context.getOptions().getSnowflake("channel");
        String message = context.getOptions().getString("message");
        Boolean instaDelete = context.getOptions().optBool("instadelete");

        return context.getEvent().getClient().getChannelById(channelId).flatMap((channel) ->
            channel.getRestChannel().createMessage(message).flatMap((m) -> {
                if (instaDelete) {
                    return channel.getRestChannel().getRestMessage(Snowflake.of(m.id())).delete("command /send said to").then();
                }
                return Mono.empty();
        }));
    }
}
