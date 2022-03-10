package com.c3po.model;

import com.c3po.helper.setting.SettingScopeTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
public class GuildRewardSettings extends BaseSettings {
    private final SettingScopeTarget target;

    private boolean enabled;

    @Builder.Default
    private final Duration timeout = Duration.ofSeconds(25);

    private int minPointsPerMessage;

    private int maxPointsPerMessage;

    public GuildRewardSettings(SettingScopeTarget target) {
        this.target = target;
    }

    public void set(String key, String value) {
        switch (key) {
            case "min_points": this.minPointsPerMessage = getInt(value);
            case "max_points": this.maxPointsPerMessage = getInt(value);
            case "enabled": this.enabled = getBool(value);
        }
    }
}
