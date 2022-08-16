package com.c3po.ui.input.base;

import com.c3po.helper.LogHelper;
import com.c3po.ui.Toast;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class MenuManager {
    private static Mono<Boolean> processEvent(ComponentInteractionEvent event, Menu menu, Replier replier) {
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
            .then(sendMessage(menu, replier))
            .then(Mono.just(menu.shouldContinue()));
    }

    private static Mono<?> replyMessage(Menu menu, Replier replier) {
        LogHelper.log("Editing msg");
        return replier.editReply()
            .withEmbedsOrNull(Collections.singleton(menu.getEmbed()))
            .withComponentsOrNull(menu.shouldContinue() ? menu.getComponents() : null);
    }

    private static Mono<?> sendMessage(Menu menu, Replier replier) {
        LogHelper.log("Sending msg");
        if (replier.isReplied()) {
            return replyMessage(menu, replier);
        }

        return replier.reply()
            .withEmbeds(menu.getEmbed())
            .withComponents(menu.getComponents())
            .onErrorResume(c -> replyMessage(menu, replier).then())
            .then(Mono.defer(() -> {
                    replier.setReplied(true);
                    return Mono.empty();
                }))
            ;
    }

    public static Mono<Menu> waitForMenu(Menu menu, Replier replier) {
        ChatInputInteractionEvent event = menu.getContext().getEvent();
        return sendMessage(menu, replier).then(
            event.getClient().on(ComponentInteractionEvent.class)
                .filter(menu::isAllowed)
                .timeout(menu.getTimeout())
                .flatMap(c -> processEvent(c, menu, replier))
                .takeWhile(c -> c)
                .then(Mono.just(menu))
                .onErrorResume(TimeoutException.class, ignore ->
                    menu.getContext().sendToast(Toast.builder().message("Menu timed out.").build()).then(Mono.just(menu)))
        );
    }

    public static Mono<Menu> waitForMenu(Menu menu) {
        return waitForMenu(menu, new Replier(menu.getContext().getEvent()));
    }
}
