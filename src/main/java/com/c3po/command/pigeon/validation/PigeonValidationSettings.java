package com.c3po.command.pigeon.validation;

import com.c3po.model.pigeon.PigeonStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PigeonValidationSettings {
    private int goldNeeded;
    private PigeonStatus requiredPigeonStatus;
    private Boolean needsActivePigeon;
    private boolean needsPvpEnabled;
    private boolean needsAvailablePvpAction;
    private boolean other;
    private Integer humanId;
    private int userId;

}
