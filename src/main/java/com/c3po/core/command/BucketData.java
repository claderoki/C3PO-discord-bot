package com.c3po.core.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BucketData {
    private int amount = 0;

    public void incrementAmount() {
        amount++;
    }
    public void decrementAmount() {
        amount--;
    }
}
