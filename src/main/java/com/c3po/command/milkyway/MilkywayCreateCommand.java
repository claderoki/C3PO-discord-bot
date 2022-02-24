package com.c3po.command.milkyway;

import com.c3po.command.Command;
import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.errors.PublicException;
import com.c3po.helper.milkyway.MilkywayCache;
import com.c3po.model.MilkywaySettings;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;


public class MilkywayCreateCommand extends Command {
    @Override
    public String getName() {
        return "milkyway create";
    }

    public void validate(MilkywaySettings settings) throws PublicException {
        if (!settings.isEnabled()) {
            throw new PublicException("This server has to have milkyways enabled first. Ask an admin to enable it first.");
        }
        if (settings.getCategoryId() == null) {
            throw new PublicException("This server has to have a milkyway category set first. Ask an admin to set it first.");
        }
        if (settings.getLogChannelId() == null) {
            throw new PublicException("This server has to have a milkyway log channel set first. Ask an admin to set it first.");
        }
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        MilkywaySettings settings = MilkywayCache.getSettings(event.getInteraction().getGuildId().orElseThrow().asLong());
        validate(settings);

//        Integer points = GuildRewardsRepository.db().getPoints();

        /*
        Scenario's:

            1 payment available:
                Continue with that payment
            2 or more payments available:
                Show an option menu with buttons allowing someone to choose one.
         */

        return Mono.empty();
    }
}
