package com.c3po.ui.input.base;

import com.c3po.helper.EmbedHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class MenuManager {

    public static Mono<?> waitForMenu(Menu menu, String message) {
        ChatInputInteractionEvent event = menu.getContext().getEvent();

        Mono<?> msg;
        try {
            msg = event.reply().withEmbeds(EmbedHelper.normal(message).build()).withComponents(menu.getComponents());
        } catch (Exception exception) {
            msg = event.editReply()
                .withEmbedsOrNull(Collections.singleton(EmbedHelper.normal(message).build()))
                .withComponentsOrNull(menu.getComponents());
        }

        return msg.then(event.getClient().on(ComponentInteractionEvent.class)
            .timeout(Duration.ofSeconds(360))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .flatMap((c) -> {
                MenuOption option = menu.matchOption(c.getCustomId());
                if (option == null) {
                    return Mono.just(true);
                }
                if (!option.shouldContinue()) {
                    return option.execute(c).then(Mono.just(false));
                }
                return option.execute(c)
                    .then(event.editReply()
                        .withEmbedsOrNull(Collections.singleton(EmbedHelper.normal(message).build()))
                        .withComponentsOrNull(menu.getComponents()))
                    .then(Mono.just(true));
            })
            .takeWhile((c) -> (boolean)c)
            .then())
        ;
    }

}
