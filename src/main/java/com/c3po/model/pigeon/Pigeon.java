package com.c3po.model.pigeon;

import com.c3po.model.pigeon.stat.Stat;
import com.c3po.model.pigeon.stat.StatType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class Pigeon {
    private int id;
    private int humanId;
    private String name;
    private Map<StatType, Stat> stats;
    private PigeonStatus status;
    private PigeonCondition condition;
    private Double goldModifier;

    public Stat getStat(StatType type) {
        return stats.get(type);
    }
}
