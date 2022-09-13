package com.c3po.command.battle.entity;

import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Player extends Entity {
    private final User user;

    public String getName() {
        return user.getUsername();
    }
}