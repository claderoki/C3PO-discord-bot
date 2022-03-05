package com.c3po.command.milkyway;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MilkywayItem {
    private int itemId;
    private int daysWorth;
    private String itemName;
    private String emoji;
}
