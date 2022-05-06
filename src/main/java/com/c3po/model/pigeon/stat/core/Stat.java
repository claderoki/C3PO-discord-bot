package com.c3po.model.pigeon.stat.core;

import com.c3po.model.pigeon.stat.StatCategory;
import com.c3po.model.pigeon.stat.StatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Stat {
    protected long  value;
    protected final StatType statType;
    protected final StatCategory statCategory;

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
