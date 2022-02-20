package com.c3po.model;

import com.c3po.helper.setting.SettingScopeTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
public class GuildRewardsSettings extends BaseModel {
    private final SettingScopeTarget target;

    private boolean enabled;

    @Builder.Default
    private final Duration timeout = Duration.ofSeconds(25);

    private int minPointsPerMessage;

    private int maxPointsPerMessage;

    public GuildRewardsSettings(SettingScopeTarget target) {
        this.target = target;
    }

    public void set(String key, String value) {
        switch (key) {
            case "min_points": this.minPointsPerMessage = Integer.parseInt(value);
            case "max_points": this.maxPointsPerMessage = Integer.parseInt(value);
            case "enabled": this.enabled = value.equals("1");
        }
    }
}
