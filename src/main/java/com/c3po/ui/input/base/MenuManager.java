package com.c3po.ui.input.base;

import com.c3po.helper.EmbedHelper;
import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class MenuManager {

    private static Mono<Boolean> processEvent(ComponentInteractionEvent event, Menu menu, String message) {
        MenuOption option = menu.matchOption(event.getCustomId());
        if (option == null) {
            return Mono.just(true);
        }
        if (!option.shouldContinue()) {
            return option.execute(event).then(Mono.just(false));
        }
        return option.execute(event)
            .then(event.editReply()
                .withEmbedsOrNull(Collections.singleton(EmbedHelper.normal(message).build()))
                .withComponentsOrNull(menu.getComponents()))
            .then(Mono.just(true));
    }

    public static Mono<?> waitForMenu(Menu menu, String message) {
        ChatInputInteractionEvent event = menu.getContext().getEvent();

        EmbedCreateSpec embed = EmbedHelper.normal(message).build();

        return event.reply()
            .withEmbeds(embed)
            .withComponents(menu.getComponents())
            .onErrorResume((c) -> event.editReply()
                .withEmbedsOrNull(Collections.singleton(embed))
                .withComponentsOrNull(menu.getComponents())
                .then())
            .then(event.getClient().on(ComponentInteractionEvent.class)
            .filter((c) -> c.getInteraction().getUser().getId().equals(event.getInteraction().getUser().getId()))
            .timeout(Duration.ofSeconds(360))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .flatMap((c) -> processEvent(c, menu, message))
            .takeWhile((c) -> c)
            .then())
        ;
    }

}
