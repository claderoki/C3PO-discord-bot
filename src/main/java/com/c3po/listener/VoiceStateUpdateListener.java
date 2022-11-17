package com.c3po.listener;

import com.c3po.helper.LogHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.spec.GuildMemberEditSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class VoiceStateUpdateListener implements EventListener<VoiceStateUpdateEvent> {
    private final static Duration TIMEOUT = Duration.ofMinutes(1);

    @Override
    public Class<VoiceStateUpdateEvent> getEventType() {
        return VoiceStateUpdateEvent.class;
    }

    private GuildMemberEditSpec disconnectSpec() {
        return GuildMemberEditSpec.builder().newVoiceChannelOrNull(null).build();
    }

    private Mono<Void> disconnect(VoiceState voiceState) {
        return voiceState.getMember()
            .flatMap(m -> m.edit(disconnectSpec()))
            .then();
    }

    private Mono<Void> disconnectIfAlone(VoiceState voiceState) {
        return voiceState.getChannel()
            .flatMap(c -> c.getVoiceStates().count())
            .filter(c -> c == 1)
            .flatMap(c -> disconnect(voiceState))
            .then();
    }

    private boolean shouldRun(VoiceStateUpdateEvent event) {
        return event.isLeaveEvent() && event.getOld().orElseThrow().getGuildId().equals(Snowflake.of(1013158959315701930L));
    }

    public Mono<Void> run(VoiceStateUpdateEvent event) {
        return event.getOld()
            .orElseThrow()
            .getChannel()
            .flatMap(c -> c.getVoiceStates().collectList())
            .filter(c -> c.size() == 1)
            .map(c -> c.get(0))
            .flatMap(c -> disconnectIfAlone(c).delaySubscription(TIMEOUT));
    }

    public Mono<Void> execute(VoiceStateUpdateEvent event) {
        try {
            if (shouldRun(event)) {
                return run(event);
            }
            return Mono.empty();
        } catch (Exception e) {
            LogHelper.log(e);
            return Mono.empty();
        }
    }
}