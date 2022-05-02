package com.c3po.model;

import com.c3po.model.pigeon.stat.Stat;
import com.c3po.model.pigeon.stat.StatType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public abstract class Winnings {
    protected LinkedHashMap<StatType, Stat> stats = new LinkedHashMap<>();
    protected List<Integer> itemIds = new ArrayList<>();

    public abstract String format();

    public Stat getStat(StatType statType) {
        return stats.get(statType);
    }

    public Stat addStat(Stat stat) {
        return stats.put(stat.getStatType(), stat);
    }

    public void addItemId(int itemId) {
        itemIds.add(itemId);
    }

    public void addItemIds(List<Integer> itemIds) {
        itemIds.addAll(itemIds);
    }

}
