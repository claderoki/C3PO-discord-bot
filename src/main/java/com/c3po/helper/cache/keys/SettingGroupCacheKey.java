package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.model.BaseSettings;

public abstract class SettingGroupCacheKey<T extends BaseSettings> extends CacheKey<T> {
    protected final SettingScopeTarget target;

    protected SettingGroupCacheKey(SettingScopeTarget target) {
        this.target = target;
    }

    public SettingScopeTarget getTarget() {
        return target;
    }

}
