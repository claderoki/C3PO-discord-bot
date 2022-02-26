package com.c3po.processors;

import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.sql.SQLException;
import java.util.HashMap;

public abstract class GuildRewardsProcessor implements Runnable {
    protected static final HashMap<String, Integer> profileIds = new HashMap<>();

    protected final GuildRewardsSettings settings;
    protected final MessageCreateEvent event;

    public GuildRewardsProcessor(GuildRewardsSettings settings, MessageCreateEvent event) {
        this.settings = settings;
        this.event = event;
    }

    protected boolean validate() {
        return settings.isEnabled() && event.getGuildId().isPresent();
    }

    protected Integer getProfileId(SettingScopeTarget target) {
        Integer profileId = profileIds.get(target.toString());
        if (profileId != null) {
            return profileId;
        }

        profileId = GuildRewardsRepository.db().getProfileId(target);
        if (profileId != null) {
            profileIds.put(target.toString(),  profileId);
            return profileId;
        }

        GuildRewardsRepository.db().createProfile(target);
        profileId = GuildRewardsRepository.db().getProfileId(target);
        profileIds.put(target.toString(),  profileId);
        return profileId;
    }

    protected abstract void _run();

    @Override
    public void run() {
        _run();
    }
}
