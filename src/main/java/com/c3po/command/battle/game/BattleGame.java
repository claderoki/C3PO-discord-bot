package com.c3po.command.battle.game;

import com.c3po.command.battle.action.core.*;
import com.c3po.command.battle.action.BreatheFire;
import com.c3po.command.battle.action.Claw;
import com.c3po.command.battle.entity.BattlePlayer;
import com.c3po.command.battle.entity.Entity;
import com.c3po.ui.input.SubMenuOption;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.SubMenu;
import discord4j.core.spec.EmbedCreateFields;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BattleGame {
    private final BattleGameState gameState;
    private final BattleUI ui;

    private List<Action> getActions(BattlePlayer player) {
        return List.of(new BreatheFire(), new Claw());
    }

    private SubMenu getPlayerSubmenu(BattlePlayer player) {
        SubMenu subMenu = null;
        for(var action: getActions(player)) {
            var option = new VoidMenuOption(action.getName());
            option.setOwnerOnly(false);
            option.setExecutor((e) -> {
                ActionContext context = new ActionContext();
                if (action instanceof AttackAction<?,?>) {
                    context = new AttackContext((AttackAction<?, ?>) action, player, player.getTarget());
                }
                ActionContext finalContext = context;
                return action.execute(context)
                    .flatMap(c -> {
                        if (c instanceof AttackResult result) {
                            return ui.notifyAttack(result, (AttackContext) finalContext);
                        } else {
                            return ui.notifyActionUsed(player.getName(), action);
                        }
                    })
                    .then(gameState.nextTurn(ui));
            });
            option.setShouldContinue(false);
            if (subMenu == null) {
                subMenu = new SubMenu(ui.getContext(), option, true);
            } else {
                subMenu.addOption(option);
            }
        }

        return subMenu;
    }

    private EmbedCreateFields.Field getField(List<? extends Entity> entities) {
        String value = entities.stream().map(Entity::getName).collect(Collectors.joining(", "));
        return EmbedCreateFields.Field.of("Side", value, false);
    }

    public Mono<Void> start() {
        Menu menu = new Menu(ui.getContext(), true);
        menu.setEmbedConsumer(e -> e.description("Battle sequence.")
            .addField(getField(gameState.getLeftSide()))
            .addField(getField(gameState.getRightSide())))
        ;

        for (Entity entity: gameState.getLeftSide()) {
            if (entity instanceof BattlePlayer player) {
                SubMenuOption option = new SubMenuOption(player.getUser().getUsername(), getPlayerSubmenu(player), true);
                option.setAllowedIf(e -> e.getInteraction().getUser().equals(player.getUser()));
                option.setDisabledIf(v -> !player.equals(gameState.getCurrent()));
                option.setEphemeral(true);
                menu.addOption(option);
            }
        }
        return new MenuManager<>(menu).waitFor().then();
    }
}