package com.c3po.model.disconnecter;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;

@Getter
public class DisconnecterSettings extends BaseSettings {
    private boolean enabled;

    public DisconnecterSettings(ScopeTarget target) {
        super(target);
    }

    public void set(String key, String value) {
        switch (key) {
            case "enabled" -> this.enabled = getBool(value);
        }
    }
}
