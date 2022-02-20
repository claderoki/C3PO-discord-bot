package com.c3po.processors;

import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Random;

public class GuildRewardsProcessor implements Runnable {
    private static final HashMap<String, Integer> profileIds = new HashMap<>();
    private static final HashMap<String, OffsetDateTime> lastRewards = new HashMap<>();

    private final GuildRewardsSettings settings;
    private final MessageCreateEvent event;

    public GuildRewardsProcessor(GuildRewardsSettings settings, MessageCreateEvent event) {
        this.settings = settings;
        this.event = event;
    }

    private boolean validate() {
        return settings.isEnabled() && event.getGuildId().isPresent();
    }

    private Integer getProfileId(SettingScopeTarget target) throws SQLException {
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

    private boolean shouldReward(SettingScopeTarget target) {
        OffsetDateTime lastRefresh = lastRewards.get(target.toString());
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(settings.getTimeout()));
    }

    private int getPointsToReward() {
        int minPoints = settings.getMinPointsPerMessage();
        int maxPoints = settings.getMaxPointsPerMessage();
        if (minPoints == maxPoints) {
            return minPoints;
        }

        Random r = new Random();
        return r.ints(minPoints, maxPoints).findFirst().orElseThrow();
    }

    private void reward(Integer profileId) throws SQLException {
        GuildRewardsRepository.db().incrementPoints(profileId, getPointsToReward());
    }

    private void _run() throws SQLException {
        if (!validate()) {
            return;
        }

        SettingScopeTarget target = SettingScopeTarget.member(
                settings.getTarget().getGuildId(),
                event.getMember().orElseThrow().getId().asLong()
        );

        if (shouldReward(target)) {
            Integer profileId = getProfileId(target);
            if (profileId != null) {
                reward(profileId);
            }
        }
    }

    @Override
    public void run() {
        try {
            _run();
        } catch (SQLException e) {
            LogHelper.logException(e);
        }
    }
}
