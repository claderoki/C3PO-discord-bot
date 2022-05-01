package com.c3po.model.pigeon.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Stat {
    private long value;

    public abstract String getEmoji();
}
