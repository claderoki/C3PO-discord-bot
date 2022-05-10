package com.c3po.ui;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Builder
@Getter
public class Toast {
    private String message;
    private Duration removeAfter;
    @Builder.Default
    private ToastType type = ToastType.ERROR;
}
