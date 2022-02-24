package com.c3po.model;

import com.c3po.helper.setting.SettingScopeTarget;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Milkyway {
    @Builder.Default
    private int id = 0;
    private SettingScopeTarget target;
    private Long channelId;
    private Integer identifier;
    private String expiresAt;
    private String description;
    private String name;
    private MilkywayStatus status;
    private String denyReason;
    private PurchaseType purchaseType;
    private Integer itemId;
    private int amount;
    private int daysPending;
    private int totalDays;
}
