package com.c3po.model.pigeon.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Stat {
    private long value;
    private StatType statType;

    public abstract String getEmoji();

    public void addValue(long value) {
        this.value += value;
    }

    public Integer getMax() {
        return null;
    }

    public Integer getMin() {
        return null;
    }

}
