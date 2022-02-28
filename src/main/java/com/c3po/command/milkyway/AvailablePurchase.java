package com.c3po.command.milkyway;

import com.c3po.model.PurchaseType;
import lombok.Builder;

@Builder
public class AvailablePurchase {
    private PurchaseType purchaseType;
    private int amount;
    private int daysWorth;
}
