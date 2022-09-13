package com.c3po.command.battle.action;

import com.c3po.command.battle.action.core.AttackAction;
import com.c3po.command.battle.action.core.AttackContext;
import com.c3po.command.battle.action.core.AttackResult;
import reactor.core.publisher.Mono;

public class BreatheFire extends AttackAction<AttackContext, AttackResult> {
    @Override
    public String getName() {
        return "Breathe fire";
    }

    @Override
    public Mono<AttackResult> execute(AttackContext context) {
        return Mono.just(AttackResult.builder().build());
    }
}
