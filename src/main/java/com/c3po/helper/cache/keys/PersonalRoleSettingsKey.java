package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.model.personalrole.PersonalRoleSettings;

import java.time.Duration;

public class PersonalRoleSettingsKey extends SettingGroupCacheKey<PersonalRoleSettings> {

    public PersonalRoleSettingsKey(ScopeTarget target) {
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
