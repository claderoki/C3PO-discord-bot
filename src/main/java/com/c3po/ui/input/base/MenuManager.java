package com.c3po.ui.input.base;

import com.c3po.ui.Toast;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class MenuManager {
    @SuppressWarnings("unchecked")
    private static Mono<Boolean> processEvent(ComponentInteractionEvent event, Menu menu, Replier replier) {
        MenuOption<?, ComponentInteractionEvent, ?> option = (MenuOption<?, ComponentInteractionEvent, ?>) menu.matchOption(event.getCustomId());
        if (option == null || !option.isAllowed(event)) {
            return Mono.just(true);
        }
        return option.execute(event)
            .then(Mono.defer(() -> {
                menu.incrementOptionsHandled();
                return sendMessage(menu, replier);
            }))
            .then(Mono.just(menu.shouldContinue()));
    }

    private static Mono<Void> editReply(Menu menu, Replier replier) {
        return Mono.defer(() -> {
            var components = menu.getComponents();
            return replier.editReply()
                .withEmbedsOrNull(Collections.singleton(menu.getEmbed()))
                .withComponentsOrNull(menu.shouldContinue() ? components : null)
                .then();
        });
    }

    private static Mono<Void> sendMessage(Menu menu, Replier replier) {
        if (replier.isReplied()) {
            return editReply(menu, replier);
        }

        return replier.reply()
            .withEmbeds(menu.getEmbed())
            .withComponents(menu.getComponents())
            .onErrorResume(c -> editReply(menu, replier))
            .then(Mono.defer(() -> {
                    replier.setReplied(true);
                    return Mono.empty();
                }))
            ;
    }

    public static Mono<Menu> waitForMenu(Menu menu, Replier replier) {
        return sendMessage(menu, replier).then(
            menu.getContext().getEvent().getClient().on(ComponentInteractionEvent.class)
                .filter(menu::isAllowed)
                .timeout(menu.getTimeout())
                .flatMap(c -> processEvent(c, menu, replier))
                .takeWhile(c -> c)
                .then(Mono.just(menu))
                .onErrorResume(TimeoutException.class, ignore ->
                    menu.getContext().sendToast(Toast.builder().message("Menu timed out.").build())
                        .then(Mono.just(menu)))
        );
    }

    public static Mono<? extends Menu> waitForMenu(Menu menu) {
        return waitForMenu(menu, new Replier(menu.getContext().getEvent()));
    }
}
