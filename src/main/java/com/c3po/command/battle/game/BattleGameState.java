package com.c3po.command.battle.game;

import com.c3po.command.battle.action.core.Action;
import com.c3po.command.battle.entity.Entity;
import com.c3po.command.battle.entity.monster.Monster;
import com.c3po.helper.Cycler;
import com.c3po.helper.RandomHelper;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
public class BattleGameState {
    private final List<? extends Entity> leftSide;
    private final Cycler<? extends Entity> leftSideCycler;
    private final List<? extends Entity> rightSide;
    private final Cycler<? extends Entity> rightSideCycler;

    private BattleSide battleSide;
    private Entity current;

    public BattleGameState(List<? extends Entity> left, List<? extends Entity> right) {
        this.leftSide = left;
        this.leftSideCycler = new Cycler<>(left);

        this.rightSide = right;
        this.rightSideCycler = new Cycler<>(right);
        battleSide = BattleSide.LEFT;
        for(Entity entity: leftSide) {
            entity.setTarget(rightSide.get(0));
        }
        for(Entity entity: rightSide) {
            entity.setTarget(leftSide.get(0));
        }
        current = getCurrentCycler().next();
    }

    private List<? extends Entity> getCurrentSide() {
        return switch (battleSide) {
            case LEFT -> leftSide;
            case RIGHT -> rightSide;
        };
    }

    private Cycler<? extends Entity> getCurrentCycler() {
        return switch (battleSide) {
            case LEFT -> leftSideCycler;
            case RIGHT -> rightSideCycler;
        };
    }

    private boolean shouldSwitch() {
        var side = getCurrentSide();
        return side.indexOf(current) == side.size()-1;
    }

    private Mono<Void> monsterTurn(BattleUI ui, Monster monster) {
        Action action = RandomHelper.choice(monster.getAvailableActions());
        return ui.notifyActionUsed(monster.getName(), action).then(Mono.defer(() -> nextTurn(ui)));
    }

    public Mono<Void> nextTurn(BattleUI ui) {
        if (shouldSwitch()) {
            battleSide = battleSide == BattleSide.LEFT ? BattleSide.RIGHT : BattleSide.LEFT;
        }
        current = getCurrentCycler().next();
        Mono<Void> mono = ui.notifyNewTurn(current);
        if (current instanceof Monster monster) {
            return mono.then(monsterTurn(ui, monster));
        }

        return mono;
    }


}
