package com.c3po.model.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ItemCategory(int id, String name, ItemCategory parent, List<ItemCategory> children, Set<Integer> itemIds) {
    /**
     * @return A Set with all current and underlying items (ids)
     */
    public Set<Integer> allItemIds() {
        Set<Integer> itemIds = new HashSet<>(itemIds());
        ItemCategory current = this;
        while (!current.children.isEmpty()) {
            for(ItemCategory child: current.children) {
                itemIds.addAll(child.itemIds);
            }
            current = current.parent;
        }
        return itemIds;
    }

    public void addChild(ItemCategory child) {
        this.children.add(child);
    }
}
