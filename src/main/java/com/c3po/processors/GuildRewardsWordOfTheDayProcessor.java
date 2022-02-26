package com.c3po.processors;

import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.sql.SQLException;

public class GuildRewardsWordOfTheDayProcessor extends GuildRewardsProcessor {

    public GuildRewardsWordOfTheDayProcessor(GuildRewardsSettings settings, MessageCreateEvent event) {
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

        Integer profileId = getProfileId(target);
    }

    @Override
    public void run() {
        _run();
    }
}
