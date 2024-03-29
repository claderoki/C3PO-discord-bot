package com.c3po.model.guildreward;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;

import java.time.Duration;

@Getter
public class GuildRewardSettings extends BaseSettings {
    private final Duration timeout = Duration.ofSeconds(25);
    private boolean enabled;
    private int minPointsPerMessage;
    private int maxPointsPerMessage;

    public GuildRewardSettings(ScopeTarget target) {
        super(target);
    }

    public void set(String key, String value) {
        switch (key) {
            case "min_points": this.minPointsPerMessage = getInt(value);
            case "max_points": this.maxPointsPerMessage = getInt(value);
            case "enabled": this.enabled = getBool(value);
        }
    }
}
