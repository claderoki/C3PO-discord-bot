package com.c3po.command.guildrewards;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class GuildRewardsSetMaxPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set maxpoints";
    }

    public String getValueParameter() {
        return "points";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        return handleSetting(event);
    }

    @Override
    String getSettingKey() {
        return "max_points_per_message";
    }
}
