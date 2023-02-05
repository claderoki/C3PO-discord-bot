package com.c3po.core.command;

import com.c3po.helper.DateTimeHelper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BucketData {
    private int amount = 0;
    private LocalDateTime createdAt = DateTimeHelper.now();

    public void incrementAmount() {
        amount++;
    }
    public void decrementAmount() {
        amount--;
    }
}
