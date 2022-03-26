package com.c3po.core;

import lombok.Getter;


@Getter
public final class ScopeTarget {
    final private Long userId;
    final private Long guildId;

    private ScopeTarget(Long userId, Long guildId) {
        this.userId = userId;
        this.guildId = guildId;
    }

    public static ScopeTarget member(Long userId, Long guildId) {
        return new ScopeTarget(userId, guildId);
    }

    public static ScopeTarget user(Long userId) {
        return new ScopeTarget(userId, null);
    }

    public static ScopeTarget guild(Long guildId) {
        return new ScopeTarget(null, guildId);
    }

    @Override
    public String toString() {
        if (userId != null && guildId != null) {
            return userId + guildId.toString();
        } else if (userId != null) {
             return userId.toString();
        } else if (guildId != null) {
            return guildId.toString();
        } else {
            return "";
        }
    }

    public Scope getScope() {
        if (guildId == null && userId != null) {
            return Scope.USER;
        } else if (guildId != null && userId != null) {
            return Scope.MEMBER;
        } else {
            return Scope.GUILD;
        }
    }

}
