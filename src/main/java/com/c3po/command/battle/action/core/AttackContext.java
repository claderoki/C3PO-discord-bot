package com.c3po.command.battle.action.core;

import com.c3po.command.battle.entity.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AttackContext extends ActionContext {
    private final AttackAction<?,?> action;
    private final Entity attacker;
    private final Entity target;
}
