package com.c3po.model;

import com.c3po.helper.setting.SettingScopeTarget;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Milkyway {
    private SettingScopeTarget target;
    private Long channelId;
    private String identifier;
    private String expiresAt;
    private String description;
    private String name;
    private MilkywayStatus status;
    private String denyReason;
    private PurchaseType purchaseType;
    private Integer itemId;
    private Integer amount;
    private Integer daysPending;
    private Integer totalDays;
}
