package com.c3po.core;

import discord4j.common.util.Snowflake;
import lombok.Getter;

import java.util.Optional;

@Getter
public final class ScopeTarget {
    private final Long userId;
    private final Long guildId;
    private final Scope scope;

    private ScopeTarget(Long userId, Long guildId, Scope scope) {
        this.userId = userId;
        this.guildId = guildId;
        this.scope = scope;
    }

    public static ScopeTarget member(long userId, long guildId) {
        return new ScopeTarget(userId, guildId, Scope.MEMBER);
    }

    public static ScopeTarget member(Snowflake userId, Snowflake guildId) {
        return member(userId.asLong(), guildId.asLong());
    }

    public static ScopeTarget user(long userId) {
        return new ScopeTarget(userId, null, Scope.USER);
    }

    public static ScopeTarget user(Snowflake userId) {
        return user(userId.asLong());
    }

    public static ScopeTarget guild(long guildId) {
        return new ScopeTarget(null, guildId, Scope.GUILD);
    }

    public static ScopeTarget guild(Snowflake guildId) {
        return guild(guildId.asLong());
    }

    @Override
    public String toString() {
        return switch (scope) {
            case GUILD -> guildId.toString();
            case USER -> userId.toString();
            case MEMBER -> userId + guildId.toString();
        };
    }

    public Optional<ScopeTarget> convert(Scope scope) {
        return switch (scope) {
            case GUILD -> Optional.ofNullable(guildId).map(ScopeTarget::guild);
            case USER -> Optional.ofNullable(userId).map(ScopeTarget::user);
            case MEMBER -> Optional.empty();
        };
    }
}
