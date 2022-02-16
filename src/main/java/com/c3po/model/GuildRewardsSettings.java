package com.c3po.model;

import lombok.Builder;

import java.time.Duration;

@lombok.Builder
@lombok.Getter
@lombok.Setter
public class GuildRewardsSettings extends BaseModel {
    @Builder.Default
    private int id = 0;
    private long guildId;
    @Builder.Default
    private Duration timeout = Duration.ofSeconds(25);
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private int minPointsPerMessage = 25;
    @Builder.Default
    private int maxPointsPerMessage = 25;
}
