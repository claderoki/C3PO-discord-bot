package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.core.ScopeTarget;
import com.c3po.model.BaseSettings;

public abstract class SettingGroupCacheKey<T extends BaseSettings> extends CacheKey<T> {
    protected final ScopeTarget target;

    protected SettingGroupCacheKey(ScopeTarget target) {
        this.target = target;
    }

    public ScopeTarget getTarget() {
        return target;
    }

}
