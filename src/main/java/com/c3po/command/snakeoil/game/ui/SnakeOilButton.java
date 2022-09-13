package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.*;
import com.c3po.ui.input.base.*;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class SnakeOilButton extends ButtonMenuOption<Void> {
    protected final GameState gameState;
    protected final SnakeOilPlayer player;

    @Setter
    protected Consumer<Void> onFinishTurn;

    public SnakeOilButton(GameState gameState, SnakeOilPlayer player) {
        super(null);
        this.gameState = gameState;
        this.player = player;
    }

    public Button modifyButton(Button button) {
        return button.disabled(!player.getTurnStatus().equals(TurnStatus.PICKING));
    }

    @Override
    public String getFullName() {
        int index = gameState.getPlayers().indexOf(player);
        String format = "[P"+(index+1)+"] %s";
        return switch (player.getStatus()) {
            case PICKING_CARD -> format.formatted("Choose product");
            case PICKING_PROFESSION -> format.formatted("Pick customer");
            case PICKING_PERSON -> format.formatted("Buy product");
            default -> format.formatted("Choose nothing..");
        };
    }

    @Override
    public boolean isAllowed(ButtonInteractionEvent event) {
        return gameState.isTest() || event.getInteraction().getUser().equals(this.player.getUser());
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        Menu menu = new Menu(context, true);
        SnakeOilMenuOption<?> option = switch (player.getStatus()) {
            case PICKING_CARD -> new CardMenuOption(gameState, player);
            case PICKING_PROFESSION -> new ProfessionMenuOption(gameState, player);
            case PICKING_PERSON -> new PersonMenuOption(gameState, player);
            default -> throw new IllegalStateException("Unexpected value: ");
        };
        menu.addOption(option);
        Interactor interactor = new Interactor(event);
        interactor.setEphemeral(true);
        return new MenuManager<>(menu, interactor).waitFor().then();
    }
}
