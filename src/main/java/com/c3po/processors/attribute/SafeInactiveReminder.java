package com.c3po.processors.attribute;

import com.c3po.core.ScopeTarget;
import com.c3po.helper.LogHelper;
import com.c3po.helper.RandomHelper;
import com.c3po.service.ActivityTrackerService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.discordjson.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SafeInactiveReminder extends Task {
    private final long safeRoleId = 852955124967276556L;
    private final long generalChannelId = 729909438378541116L;

    private final ActivityTrackerService activityTrackerService;

    public Mono<Integer> executeForGuild(Guild guild) {
        var settings = activityTrackerService.getSettings(ScopeTarget.guild(guild.getId()));
        return activityTrackerService.getInactiveMembers(guild, Duration.ofDays(settings.getDaysToBeInactive()))
            .filter(m -> m.getMember().getMemberData().roles().contains(Id.of(safeRoleId)))
            .collectList()
            .filter(l -> !l.isEmpty())
            .map(RandomHelper::choice)
            .map(m -> {
                LogHelper.log("MEMEBER IS " + m.getMember().getUsername());
                return m;
            })
            .then(Mono.just(1))
            ;
    }

    public Mono<Void> execute(GatewayDiscordClient client) {
        return client.getGuilds()
            .filter(c -> c.getId().equals(Snowflake.of(729843647347949638L)))
            .flatMap(this::executeForGuild)
            .filter(c -> c > 0)
            .doOnNext(i -> LogHelper.log(i + " mememe(s) purged"))
            .then();
    }

    @Override
    public Duration getDelay() {
        return Duration.ofHours(1);
    }
}
