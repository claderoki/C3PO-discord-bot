package com.c3po.command.battle.entity.monster;

import com.c3po.command.battle.action.core.Action;
import com.c3po.command.battle.action.Stab;

import java.util.List;

public class Slime extends Monster {
    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public List<Action<?,?>> getAvailableActions() {
        return List.of(new Stab());
    }
}
