package com.c3po.command.battle.entity.monster;

import com.c3po.command.battle.action.core.Action;
import com.c3po.command.battle.entity.Entity;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Monster extends Entity {
    public abstract List<Action<?, ?>> getAvailableActions();
}
