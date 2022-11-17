package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.model.disconnecter.DisconnecterSettings;
import com.c3po.model.personalrole.PersonalRoleSettings;

import java.time.Duration;

public class DisconnecterSettingsKey extends SettingGroupCacheKey<DisconnecterSettings> {

    public DisconnecterSettingsKey(ScopeTarget target) {
        super(target);
    }

    @Override
    public String getKeyAffix() {
        return target.toString();
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }

}
