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
    public Mono<?> execute(Context context) throws RuntimeException {
        Snowflake channelId = context.getOptions().getSnowflake("channel");
        String message = context.getOptions().getString("message");
        Boolean instaDelete = context.getOptions().optBool("instadelete");

        context.getEvent().getClient().getChannelById(channelId).subscribe((channel) ->
            channel.getRestChannel().createMessage(message).subscribe((m) -> {
                if (instaDelete) {
                    channel.getRestChannel().getRestMessage(Snowflake.of(m.id())).delete("command /send said to").subscribe();
                }
        }));

        return context.getEvent().deferReply();
    }
}
