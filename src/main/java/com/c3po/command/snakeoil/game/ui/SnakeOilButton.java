package com.c3po.command.snakeoil.game.ui;

import com.c3po.command.snakeoil.game.*;
import com.c3po.ui.input.base.*;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
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
        super("abc");
        this.gameState = gameState;
        this.player = player;
    }

    public Button modifyButton(Button button) {
        return button.disabled(!player.getTurnStatus().equals(TurnStatus.PICKING));
    }

    public PlayerStatus getStatus() {
        boolean allWordsChosen = gameState.getPlayers()
            .stream()
            .filter(c -> c != gameState.getCurrentRound().getCustomer())
            .allMatch(c -> c.getTurnStatus().equals(TurnStatus.FINISHED));

        if (gameState.getCurrentRound().getCustomer().equals(player)) {
            if (allWordsChosen) {
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
            default -> "nothing..";
        };
        return ind + " Choose " + type;
    }

    @Override
    protected boolean isAllowed(ComponentInteractionEvent event) {
        return event.getInteraction().getUser().equals(this.player.getUser());
    }

    @Override
    public Mono<?> execute(ButtonInteractionEvent event) {
        Menu menu = new Menu(context);
        menu.setEmbedConsumer(e -> e.description("You must choose"));
        PlayerStatus status = getStatus();
        MenuOption<?, ?, ?> option = switch (status) {
            case PICKING_CARD -> new CardMenuOption(gameState, player);
            case PICKING_PROFESSION -> new ProfessionMenuOption(gameState, player);
            case PICKING_PERSON -> new PersonMenuOption(gameState, player);
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
        menu.addOption(option);
        Replier replier = new Replier(event);
        replier.setEphemeral(true);
        return MenuManager.waitForMenu(menu, replier).map(m -> {
            if (status == PlayerStatus.PICKING_PERSON) {
                onFinishTurn.accept(null);
            }
            return Mono.empty();
        });
    }

}
