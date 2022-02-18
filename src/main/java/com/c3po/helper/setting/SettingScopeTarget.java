package com.c3po.helper.setting;

import lombok.Getter;

@Getter
public final class SettingScopeTarget {
    final private Long userId;
    final private Long guildId;

    private SettingScopeTarget(Long userId, Long guildId) {
        this.userId = userId;
        this.guildId = guildId;
    }

    public static SettingScopeTarget member(Long userId, Long guildId) {
        return new SettingScopeTarget(userId, guildId);
    }

    public static SettingScopeTarget user(Long userId) {
        return new SettingScopeTarget(userId, null);
    }

    public static SettingScopeTarget guild(Long guildId) {
        return new SettingScopeTarget(null, guildId);
    }
}
