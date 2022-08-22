package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.GameState;
import com.c3po.command.snakeoil.game.SnakeOilPlayer;
import com.c3po.helper.EmbedHelper;
import com.c3po.ui.input.base.SelectMenuMenuOption;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.spec.InteractionFollowupCreateMono;
import reactor.core.publisher.Mono;

public abstract class SnakeOilMenuOption<T> extends SelectMenuMenuOption<T> {
    protected final GameState gameState;
    protected final SnakeOilPlayer player;

    @Override
    protected boolean shouldContinue() {
        return false;
    }

    public SnakeOilMenuOption(String name, GameState gameState, SnakeOilPlayer player) {
        super(name);
        this.gameState = gameState;
        this.player = player;
    }

    protected abstract void afterHook();

    protected abstract String getFollowupDescription();

    protected InteractionFollowupCreateMono followup(InteractionFollowupCreateMono followup) {
        SnakeOilPlayer player = gameState.getCurrentlyPicking();
        return followup
            .withContent(player.getUser().getMention() + ", your turn!")
            .withEmbeds(EmbedHelper.notice(getFollowupDescription()).build())
        ;
    }

    @Override
    final public Mono<Void> execute(SelectMenuInteractionEvent event) {
        return super.execute(event)
            .then(Mono.defer(() -> {
                afterHook();
                gameState.nextPicking();
                return followup(context.getEvent().createFollowup())
                    .flatMap(m -> {
                        Mono<?> mono = Mono.empty();
                        if (gameState.getPreviousNotification() != null) {
                            mono = gameState.getPreviousNotification().delete();
                        }
                        gameState.setPreviousNotification(m);
                        return mono;
                    });
            }))
            .then(event.deferEdit());
    }
}
