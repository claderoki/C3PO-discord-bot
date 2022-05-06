package com.c3po.service;

import com.c3po.connection.repository.ItemRepository;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.ItemCategoriesKey;
import com.c3po.model.item.ItemCategory;

import java.util.Map;

public class ItemService {
    public static Map<Integer, ItemCategory> getAllCategories() {
        return Cache.computeIfAbsent(new ItemCategoriesKey(), key -> ItemRepository.db().getAllCategories());
    }
}
