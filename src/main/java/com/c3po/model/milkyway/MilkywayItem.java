package com.c3po.model.milkyway;

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
