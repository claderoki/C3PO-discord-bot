package com.c3po.ui.input.base;

import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class MenuManager {
    private static Mono<Boolean> processEvent(ComponentInteractionEvent event, Menu menu) {
        MenuOption<?, ComponentInteractionEvent, ?> option = (MenuOption<?, ComponentInteractionEvent, ?>) menu.matchOption(event.getCustomId());
        if (option == null || !option.isAllowed(event)) {
            return Mono.just(true);
        }
        if (!option.shouldContinue()) {
            return option.execute(event)
                .map(e -> {
                    menu.incrementOptionsHandled();
                    return Mono.empty();
                })
                .then(Mono.just(false));
        }
        return option.execute(event)
            .map(e -> {
                menu.incrementOptionsHandled();
                return Mono.empty();
            })
            .then(event.editReply()
                .withEmbedsOrNull(Collections.singleton(menu.getEmbed()))
                .withComponentsOrNull(menu.getComponents()))
            .then(Mono.just(menu.shouldContinue()));
    }

    private static Mono<?> sendMessage(Menu menu, ChatInputInteractionEvent event) {
        LogHelper.log("SENDING");
        return event.reply()
            .withEmbeds(menu.getEmbed())
            .withComponents(menu.getComponents())
            .onErrorResume(c -> event.editReply()
                .withEmbedsOrNull(Collections.singleton(menu.getEmbed()))
                .withComponentsOrNull(menu.getComponents())
                .then())
            .map(c -> {
                LogHelper.log("HMM");
                return Mono.empty();
            })
        ;
    }

    public static Mono<?> waitForMenu(Menu menu) {
        ChatInputInteractionEvent event = menu.getContext().getEvent();
        return sendMessage(menu, event)
            .then(event.getClient().on(ComponentInteractionEvent.class)
                .filter(menu::isAllowed)
                .timeout(menu.getTimeout())
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .flatMap(c -> processEvent(c, menu))
                .takeWhile(c -> c)
                .then())
        ;
    }
}
