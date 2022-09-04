package com.c3po.command.insecta.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class InsectaProfile {
    private Integer id;
    private long hexacoin;
    private LocalDateTime lastCollected;
    private long userId;
    private Insectarium insectarium;
}
