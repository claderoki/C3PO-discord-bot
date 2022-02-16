package com.c3po.ui;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.command.Interaction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

@lombok.Setter
public class ButtonWaiter {
    private boolean currently = false;
    private Snowflake userId;
    private Snowflake messageId;
    private ArrayList<BaseButton> buttons;

    public ButtonWaiter(Interaction interaction) {
        this.userId = interaction.getUser().getId();
        if (interaction.getMessageId().isPresent()) {
            this.messageId = interaction.getMessageId().get();
        }
    }

    public Mono<Void> wait(GatewayDiscordClient client) throws Exception {
        return client.on(ButtonInteractionEvent.class, buttonEvent -> {
            if (currently) {
                return Mono.empty();
            }
            if (buttonEvent.getInteraction().getUser().getId().equals(this.userId))
                for(BaseButton button: buttons) {
                    if (buttonEvent.getCustomId().equals(button.getCode())) {
                        try {
                            currently = true;
                            return button.handle(buttonEvent).doFinally(c -> currently = false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            return Mono.empty();
        }).timeout(Duration.ofMinutes(1))
        .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
        .then();
    }


}
