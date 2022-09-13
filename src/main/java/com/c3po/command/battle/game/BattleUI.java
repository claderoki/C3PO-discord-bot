package com.c3po.command.battle.game;

import com.c3po.command.battle.action.core.Action;
import com.c3po.command.battle.action.core.ActionContext;
import com.c3po.command.battle.action.core.AttackContext;
import com.c3po.command.battle.action.core.AttackResult;
import com.c3po.command.battle.entity.Entity;
import com.c3po.core.command.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Getter
public class BattleUI {
    private final Context context;

    public Mono<Void> notifyActionUsed(String who, Action action) {
        String message = who  + " used `" + action.getName() + "`";
        return context.getInteractor()
            .followup(f -> f.withContent(message), 2, "actionUsed")
            .then()
            ;
    }

    public Mono<Void> notifyNewTurn(Entity entity) {
        String message = "It's %s's turn.".formatted(entity.getName());
        return context.getInteractor()
            .followup(f -> f.withContent(message), 1, "newTurnNotified")
            .then()
            ;
    }

    public Mono<Void> notifyAttack(AttackResult result, AttackContext context) {
        String msg = "%s attacks %s for %s damage.";
        String message = context.getAttacker().getName()  + " attacks " + context.getTarget().getName() + " for " + result.getDamageDealt() + " damage.";
        return this.context.getInteractor().followup(f -> f.withContent(message), 2, "actionUsed").then();
    }

}
