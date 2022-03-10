package com.c3po.processors;

import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class GuildRewardWordOfTheDayProcessor extends GuildRewardProcessor {

    public GuildRewardWordOfTheDayProcessor(GuildRewardSettings settings, MessageCreateEvent event) {
        super(settings, event);
    }

    protected void _run() {
        if (!validate()) {
            return;
        }

        SettingScopeTarget target = SettingScopeTarget.member(
                settings.getTarget().getGuildId(),
                event.getMember().orElseThrow().getId().asLong()
        );
    }

    @Override
    public void run() {
        _run();
    }
}
