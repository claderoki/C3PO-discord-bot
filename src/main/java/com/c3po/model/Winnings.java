package com.c3po.model;

import com.c3po.model.pigeon.stat.core.Stat;
import com.c3po.model.pigeon.stat.StatType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Winnings {
    protected LinkedHashMap<StatType, Stat> stats = new LinkedHashMap<>();
    protected List<Integer> itemIds = new ArrayList<>();

    public abstract String format();

    public Stat getStat(StatType statType) {
        return stats.get(statType);
    }

    public void addStat(Stat stat) {
        stats.put(stat.getStatType(), stat);
    }

    public void addItemId(int itemId) {
        itemIds.add(itemId);
    }

    public void addItemIds(List<Integer> itemIds) {
        this.itemIds.addAll(itemIds);
    }

    public void setStats(List<Stat> stats) {
        this.stats = new LinkedHashMap<>(stats.stream().collect(Collectors.toMap(Stat::getStatType, (v) -> v)));
    }

    public void setStats(LinkedHashMap<StatType, Stat> stats) {
        this.stats = stats;
    }

}
