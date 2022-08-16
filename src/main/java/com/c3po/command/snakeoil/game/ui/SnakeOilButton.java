package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.*;
import com.c3po.helper.LogHelper;
import com.c3po.ui.input.base.*;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.Button;
import reactor.core.publisher.Mono;

public class SnakeOilButton extends ButtonMenuOption<Void> {
    protected final GameState gameState;
    protected final SnakeOilPlayer player;
    protected final SnakeOilUI ui;

    public SnakeOilButton(GameState gameState, SnakeOilPlayer player, SnakeOilUI ui) {
        super("abc");
        this.gameState = gameState;
        this.player = player;
        this.ui = ui;
    }

    private boolean isDisabled() {
        boolean isProfessionChosen = (gameState.getChosenProfession() == null);
        if (player.equals(gameState.getTurn())) {
            return !isProfessionChosen && !gameState.getStatuses().values().stream().allMatch(c -> c.equals(TurnStatus.FINISHED));
        } else {
            return isProfessionChosen;
        }
    }

    public Button modifyButton(Button button) {
        return button.disabled(isDisabled());
    }

    public PlayerStatus getStatus() {
        if (player.equals(gameState.getTurn())) {
            if (gameState.getStatuses().values().stream().allMatch(c -> c.equals(TurnStatus.FINISHED))) {
                return PlayerStatus.PICKING_PERSON;
            } else {
                return PlayerStatus.PICKING_PROFESSION;
            }
        } else {
            return PlayerStatus.PICKING_CARD;
        }
    }

    @Override
    public String getFullName() {
        int index = gameState.getPlayers().indexOf(player);
        String ind = "["+(index+1)+"]";
        PlayerStatus status = getStatus();
        String type = switch (status) {
            case PICKING_CARD -> "cards";
            case PICKING_PROFESSION -> "profession";
            case PICKING_PERSON -> "winner";
        };
        return ind + " Choose " + type;
    }

    @Override
    protected boolean isAllowed(ComponentInteractionEvent event) {
        return event.getInteraction().getUser().equals(this.player.user());
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        LogHelper.log("starting button time");
        Menu menu = new Menu(context);
        menu.setEmbedConsumer(e -> e.description("You must choose"));
        PlayerStatus status = getStatus();
        MenuOption<?, ?, ?> option = switch (status) {
            case PICKING_CARD -> new CardMenuOption(gameState, player);
            case PICKING_PROFESSION -> new ProfessionMenuOption(gameState, player);
            case PICKING_PERSON -> new PersonMenuOption(gameState, player);
        };
        LogHelper.log("button time");
        menu.addOption(option);
        Replier replier = new Replier(event);
        replier.setEphemeral(true);
        return MenuManager.waitForMenu(menu, replier).map(m -> {
            if (status == PlayerStatus.PICKING_PERSON) {
                gameState.newTurn(menu, ui);
            }
            return Mono.empty();
        });
    }

}
