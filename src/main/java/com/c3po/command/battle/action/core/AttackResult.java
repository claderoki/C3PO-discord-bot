package com.c3po.command.battle.action.core;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AttackResult extends ActionResult {
    private int damageDealt;
}
