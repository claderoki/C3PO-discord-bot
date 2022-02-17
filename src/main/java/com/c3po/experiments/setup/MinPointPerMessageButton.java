package com.c3po.experiments.setup;

import com.c3po.model.GuildRewardsSettings;
import com.c3po.ui.BaseButton;
import com.c3po.ui.IntWaiter;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MinPointPerMessageButton extends BaseButton {
    GuildRewardsSettings settings;

    public String getCode() {
        return "min-points-per-message";
    }

    public String getLabel() {
        return "Min points per message";
    }

    @Override
    public Mono<Void> handle(ButtonInteractionEvent event) throws Exception {
        event.reply().withContent("Enter a number.").block();
        IntWaiter waiter = new IntWaiter(event.getInteraction());
        return waiter.handle().doFinally((c) -> settings.setMinPointsPerMessage(waiter.getValue()));
    }

}
