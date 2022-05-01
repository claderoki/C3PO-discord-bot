package com.c3po.core.command;

import com.c3po.core.Scope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Bucket {
    private final Scope per;
    private final int amount;
}
