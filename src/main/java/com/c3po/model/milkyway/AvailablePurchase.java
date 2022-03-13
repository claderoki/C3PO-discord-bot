package com.c3po.model.milkyway;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AvailablePurchase {
    private PurchaseType purchaseType;
    private long amount;
    private int daysWorth;
    private String label;
    private String emoji;
    private MilkywayItem item;
}
