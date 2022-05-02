package com.c3po.model.exploration;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Exploration {
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime arrivalDate;
    private int actionsRemaining;
    private int totalActions;
    private int locationId;
}
