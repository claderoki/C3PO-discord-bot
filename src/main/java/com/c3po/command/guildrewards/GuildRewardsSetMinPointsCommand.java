package com.c3po.command.guildrewards;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;


public class GuildRewardsSetMinPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set minpoints";
    }

    public String getValueParameter() {
        return "points";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        return handleSetting(event);
    }

    @Override
    String getSettingKey() {
        return "min_points_per_message";
    }
}
