package com.c3po.model;

import com.c3po.core.ScopeTarget;
import com.c3po.helper.ValueParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseSettings implements ValueParser {
    protected final ScopeTarget target;

    public String optString(String value) {
        return value;
    }

    public abstract void set(String key, String value);
}
