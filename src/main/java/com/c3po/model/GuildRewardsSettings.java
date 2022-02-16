package com.c3po.model;

import java.time.Duration;

@lombok.Builder
@lombok.Getter
public class GuildRewardsSettings extends BaseModel {
    private int id = 0;
    private long guildId;
    private Duration timeout = Duration.ofSeconds(25);
    private boolean enabled = true;
    private int minPointsPerMessage = 25;
    private int maxPointsPerMessage = 25;
}
