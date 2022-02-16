package com.c3po.command.guildrewards.setup;

import com.c3po.model.GuildRewardsSettings;
import com.c3po.ui.BaseButton;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class TimeoutButton extends BaseButton {
    GuildRewardsSettings settings;

    public String getCode() {
        return "timeout";
    }

    public String getLabel() {
        return "Timeout";
    }

    @Override
    public Mono<Void> handle(ButtonInteractionEvent event) throws Exception {
        return null;
    }

}
