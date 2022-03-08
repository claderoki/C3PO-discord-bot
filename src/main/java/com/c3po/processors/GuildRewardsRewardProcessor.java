package com.c3po.processors;

import com.c3po.connection.repository.GuildRewardsRepository;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.GuildRewardsSettings;
import com.c3po.service.GuildRewardService;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Random;

public class GuildRewardsRewardProcessor extends GuildRewardsProcessor {
    private static final HashMap<String, OffsetDateTime> lastRewards = new HashMap<>();

    public GuildRewardsRewardProcessor(GuildRewardsSettings settings, MessageCreateEvent event) {
        super(settings, event);
    }

    protected boolean shouldReward(SettingScopeTarget target) {
        OffsetDateTime lastRefresh = lastRewards.get(target.toString());
        return lastRefresh == null || OffsetDateTime.now(ZoneOffset.UTC).isAfter(lastRefresh.plus(settings.getTimeout()));
    }

    protected int getPointsToReward() {
        int minPoints = settings.getMinPointsPerMessage();
        int maxPoints = settings.getMaxPointsPerMessage();
        if (minPoints == maxPoints) {
            return minPoints;
        }

        Random r = new Random();
        return r.ints(minPoints, maxPoints).findFirst().orElseThrow();
    }

    protected void reward(Integer profileId) {
        GuildRewardsRepository.db().incrementPoints(profileId, getPointsToReward());
    }

    protected void _run() {
        if (!validate()) {
            return;
        }

        SettingScopeTarget target = SettingScopeTarget.member(
                settings.getTarget().getGuildId(),
                event.getMember().orElseThrow().getId().asLong()
        );

        if (shouldReward(target)) {
            Integer profileId = GuildRewardService.getProfileId(target);
            if (profileId != null) {
                reward(profileId);
                lastRewards.put(target.toString(), DateTimeHelper.offsetNow());
            }
        }
    }

    @Override
    public void run() {
        try {
            _run();
        } catch (Exception e) {
            LogHelper.logException(e);
        }
    }
}
