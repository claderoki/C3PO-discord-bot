package com.c3po.command.guildrewards;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class GuildRewardsToggleCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " toggle";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        if (event.getInteraction().getGuildId().isEmpty()) {
            return Mono.empty();
        }
        long guildId = event.getInteraction().getGuildId().get().asLong();

        return Mono.empty();
    }

    @Override
    String getSettingKey() {
        return "enabled";
    }
}
