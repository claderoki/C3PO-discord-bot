package com.c3po.command.guildrewards.settings;

import com.c3po.model.GuildRewardsSettings;
import com.c3po.ui.IntWaiter;
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MaxPointsPerMessage extends BaseSetting<Integer> {
    private GuildRewardsSettings settings;

    @Override
    void hydrateValue(Integer value) {
        settings.setMaxPointsPerMessage(value);
    }

    @Override
    IntWaiter getWaiter(DeferrableInteractionEvent event) {
        return new IntWaiter(event.getInteraction());
    }

    @Override
    InteractionApplicationCommandCallbackReplyMono hydratePrompt(InteractionApplicationCommandCallbackReplyMono reply) {
        return reply.withContent(getQuestion()).withEmbeds();
    }

    String getQuestion() {
        return "What will the max points per message be?";
    }

}
