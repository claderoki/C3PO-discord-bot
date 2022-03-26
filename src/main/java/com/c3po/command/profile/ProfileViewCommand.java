package com.c3po.command.profile;

import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.helper.DiscordCommandOptionType;
import discord4j.common.util.Snowflake;
import reactor.core.publisher.Mono;

public class ProfileViewCommand extends SubCommand {
    protected ProfileViewCommand(ProfileCommandGroup group) {
        super(group, "view", "View your own, or someone else's profile.");
        this.addOption(option -> option.name("member")
            .description("The member who's profile you'd like to view.")
            .required(false)
            .type(DiscordCommandOptionType.USER.getValue()));
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        Snowflake userId = context.getOptions().getSnowflake("member");



        return Mono.empty();
    }

}
