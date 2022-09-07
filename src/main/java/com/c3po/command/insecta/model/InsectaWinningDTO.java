package com.c3po.command.insecta.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InsectaWinningDTO {
    private String key;
    private long userId;
    private long value;
    @Builder.Default
    private boolean collected = false;
    private LocalDateTime initialDate;
}
