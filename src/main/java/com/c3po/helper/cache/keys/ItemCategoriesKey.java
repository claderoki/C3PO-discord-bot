package com.c3po.helper.cache.keys;

import com.c3po.helper.cache.CacheKey;
import com.c3po.model.item.ItemCategory;

import java.time.Duration;
import java.util.Map;

public class ItemCategoriesKey extends CacheKey<Map<Integer, ItemCategory>> {
    @Override
    public String getKeyAffix() {
        return "";
    }

    @Override
    public Duration getTimeToLive() {
        return Duration.ofHours(10);
    }
}
