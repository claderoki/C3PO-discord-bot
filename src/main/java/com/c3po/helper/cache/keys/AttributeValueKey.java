package com.c3po.helper.cache.keys;

import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.CacheKey;
import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public class AttributeValueKey extends CacheKey<PropertyValue> {
    private final ScopeTarget target;
    private final int attributeId;

    @Override
    public String getKeyAffix() {
        return target.toString() + ":" + attributeId;
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(1);
    }
}
