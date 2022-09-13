package com.c3po.command.battle.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Entity {
    public abstract String getName();
    private Entity target;
    private int health;
    private int maxHealth;
}