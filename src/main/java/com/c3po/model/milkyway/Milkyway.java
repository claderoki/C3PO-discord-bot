package com.c3po.model.milkyway;

import com.c3po.core.ScopeTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Milkyway {
    @Builder.Default
    private int id = 0;
    private ScopeTarget target;
    private Long channelId;
    private Long identifier;
    private LocalDateTime expiresAt;
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
