package com.c3po.model.milkyway;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;

@Getter
public class MilkywaySettings extends BaseSettings {
    private boolean enabled;
    private int costPerDay;
    private Long categoryId;
    private Long logChannelId;
    private int activeLimit;
    private boolean godmode;

    public MilkywaySettings(ScopeTarget target) {
        super(target);
    }

    public void set(String key, String value) {
        switch (key) {
            case "enabled" -> this.enabled = getBool(value);
            case "godmode" -> this.godmode = getBool(value);
            case "cost_per_day" -> this.costPerDay = getInt(value);
            case "category_id" -> this.categoryId = optLong(value);
            case "log_channel_id" -> this.logChannelId = optLong(value);
            case "active_limit" -> this.activeLimit = getInt(value);
        }
    }
}
