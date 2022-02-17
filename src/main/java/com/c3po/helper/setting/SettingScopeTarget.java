package com.c3po.helper.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SettingScopeTarget {
    final private Long userId;
    final private Long guildId;

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
