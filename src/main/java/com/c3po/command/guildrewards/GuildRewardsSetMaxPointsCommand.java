package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.helper.InteractionHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

public class GuildRewardsSetMaxPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set maxpoints";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        if (event.getInteraction().getGuildId().isEmpty()) {
            return Mono.empty();
        }
        long guildId = event.getInteraction().getGuildId().get().asLong();
        ApplicationCommandInteractionOptionValue value = InteractionHelper.getOptionValue(event, "points");

        return Mono.empty();
    }

    @Override
    String getSettingKey() {
        return "max_points_per_message";
    }
}
