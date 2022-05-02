package com.c3po.command.pigeon;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PigeonValidationResult {
    private boolean shouldNotifyDeath;
    private boolean hasGoldNeeded;
    private Integer pigeonId;
    private int humanId;
    private boolean hasRequiredStatus;
    private boolean hasAvailablePvpAction;
    private boolean hasPvpEnabled;

    public boolean isHasActivePigeon() {
        return pigeonId != null;
    }
}
