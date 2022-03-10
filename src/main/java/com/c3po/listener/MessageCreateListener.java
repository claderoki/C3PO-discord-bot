package com.c3po.listener;

import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardSettings;
import com.c3po.processors.GuildRewardRewardProcessor;
import com.c3po.service.GuildRewardService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class MessageCreateListener {
    private static Mono<Void> _handle(MessageCreateEvent event) {
        //TODO: when message events start piling up, turn these into async handlers similar to CommandListener.
        if (event.getMember().isPresent() && event.getMember().get().isBot()) {
            return Mono.empty();
        }

        if (event.getGuildId().isPresent()) {
            long guildId = event.getGuildId().get().asLong();
            SettingScopeTarget target = SettingScopeTarget.guild(guildId);
            GuildRewardSettings guildRewardsSettings = GuildRewardService.getSettings(target);
            GuildRewardRewardProcessor guildRewardsProcessor = new GuildRewardRewardProcessor(guildRewardsSettings, event);
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
