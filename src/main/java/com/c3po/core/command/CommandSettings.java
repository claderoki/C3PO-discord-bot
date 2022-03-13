package com.c3po.core.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommandSettings {
    private boolean adminOnly;
    private boolean guildOnly;
}
