package com.c3po.listener;

import com.c3po.helper.guildrewards.GuildRewardsCache;
import com.c3po.helper.LogHelper;
import com.c3po.model.GuildRewardsSettings;
import com.c3po.processors.GuildRewardsRewardProcessor;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class MessageCreateListener {
    private static Mono<Void> _handle(MessageCreateEvent event) throws Exception {
        if (event.getMember().isPresent() && event.getMember().get().isBot()) {
            return Mono.empty();
        }

        if (event.getGuildId().isPresent()) {
            long guildId = event.getGuildId().get().asLong();
            GuildRewardsSettings guildRewardsSettings = GuildRewardsCache.getSettings(guildId);
            GuildRewardsRewardProcessor guildRewardsProcessor = new GuildRewardsRewardProcessor(guildRewardsSettings, event);
            guildRewardsProcessor.run();
        }

        return Mono.empty();
    }

    public static Mono<Void> handle(MessageCreateEvent event) {
        try {
            return _handle(event);
        } catch (Exception e) {
            LogHelper.logException(e);
            return Mono.empty();
        }
    }
}
