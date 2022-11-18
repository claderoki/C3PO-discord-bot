package com.c3po.model.guildreward;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActivityTrackerSettings extends BaseSettings {
    private final ScopeTarget target;

    private boolean enabled;

    public void set(String key, String value) {
        switch (key) {
            case "enabled" -> this.enabled = getBool(value);
        }
    }
}
