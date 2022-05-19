package com.c3po.service;

import com.c3po.connection.repository.ItemRepository;
import com.c3po.helper.cache.keys.ItemCategoriesKey;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.item.ItemCategory;

import java.util.Map;

public class ItemService {
    private final ItemRepository itemRepository = ItemRepository.db();

    public Map<Integer, ItemCategory> getAllCategories() {
        return CacheManager.get().computeIfAbsent(new ItemCategoriesKey(), key -> itemRepository.getAllCategories());
    }
}
