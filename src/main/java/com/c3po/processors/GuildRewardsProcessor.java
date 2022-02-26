package com.c3po.processors;

import com.c3po.model.GuildRewardsSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class GuildRewardsProcessor implements Runnable {

    protected final GuildRewardsSettings settings;
    protected final MessageCreateEvent event;

    public GuildRewardsProcessor(GuildRewardsSettings settings, MessageCreateEvent event) {
        this.settings = settings;
        this.event = event;
    }

    protected boolean validate() {
        return settings.isEnabled() && event.getGuildId().isPresent();
    }

    protected abstract void _run();

    @Override
    public void run() {
        _run();
    }
}
