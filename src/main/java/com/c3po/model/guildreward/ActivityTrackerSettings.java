package com.c3po.model.guildreward;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;

@Getter
public class ActivityTrackerSettings extends BaseSettings {
    public ActivityTrackerSettings(ScopeTarget target) {
        super(target);
    }

    private boolean enabled;
    private int daysToBeInactive;

    public void set(String key, String value) {
        switch (key) {
            case "enabled" -> this.enabled = getBool(value);
            case "days_for_inactive" -> this.daysToBeInactive = getInt(value);
        }
    }
}
