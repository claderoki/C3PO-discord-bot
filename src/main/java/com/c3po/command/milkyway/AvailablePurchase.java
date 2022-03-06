package com.c3po.command.milkyway;

import com.c3po.model.PurchaseType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AvailablePurchase {
    private PurchaseType purchaseType;
    private int amount;
    private int daysWorth;
    private String label;
    private String emoji;
    private MilkywayItem item;
}
