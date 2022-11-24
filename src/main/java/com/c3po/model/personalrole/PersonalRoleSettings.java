package com.c3po.model.personalrole;

import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;
import lombok.Getter;

@Getter
public class PersonalRoleSettings extends BaseSettings {
    private boolean enabled;
    private Long roleId;

    public PersonalRoleSettings(ScopeTarget target) {
        super(target);
    }

    public void set(String key, String value) {
        switch (key) {
            case "enabled" -> this.enabled = getBool(value);
            case "role_id" -> this.roleId = getLong(value);
        }
    }
}
