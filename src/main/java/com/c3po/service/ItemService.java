package com.c3po.service;

import com.c3po.connection.repository.ItemRepository;
import com.c3po.helper.cache.keys.ItemCategoriesKey;
import com.c3po.helper.cache.CacheManager;
import com.c3po.model.item.ItemCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Map<Integer, ItemCategory> getAllCategories() {
        return CacheManager.get().computeIfAbsent(new ItemCategoriesKey(), key -> itemRepository.getAllCategories());
    }
}
