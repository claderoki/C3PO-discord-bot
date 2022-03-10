package com.c3po.processors;

import com.c3po.model.GuildRewardSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class GuildRewardProcessor implements Runnable {

    protected final GuildRewardSettings settings;
    protected final MessageCreateEvent event;

    public GuildRewardProcessor(GuildRewardSettings settings, MessageCreateEvent event) {
        this.settings = settings;
        this.event = event;
    }

    protected boolean validate() {
        return settings.isEnabled() && event.getGuildId().isPresent() && event.getMember().isPresent();
    }

    protected abstract void _run();

    @Override
    public void run() {
        _run();
    }
}
