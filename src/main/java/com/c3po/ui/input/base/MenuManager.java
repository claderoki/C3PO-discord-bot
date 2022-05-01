package com.c3po.ui.input.base;

import com.c3po.helper.EmbedHelper;
import com.c3po.helper.LogHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.Embed;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class MenuManager {

    private static Mono<Boolean> processEvent(ComponentInteractionEvent event, Menu menu, EmbedCreateSpec embed) {
        MenuOption option = menu.matchOption(event.getCustomId());
        if (option == null) {
            return Mono.just(true);
        }
        if (!option.shouldContinue()) {
            return option.execute(event).then(Mono.just(false));
        }
        return option.execute(event)
            .then(event.editReply()
                .withEmbedsOrNull(Collections.singleton(embed))
                .withComponentsOrNull(menu.getComponents()))
            .then(Mono.just(true));
    }

    public static Mono<?> waitForMenu(Menu menu, String message) {
        return waitForMenu(menu, (e) -> e.description(message));
    }

    public static Mono<?> waitForMenu(Menu menu, Consumer<EmbedCreateSpec.Builder> embedConsumer) {
        ChatInputInteractionEvent event = menu.getContext().getEvent();

        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
        embed.color(EmbedHelper.COLOR);
        embedConsumer.accept(embed);

        return event.reply()
            .withEmbeds(embed.build())
            .withComponents(menu.getComponents())
            .onErrorResume((c) -> event.editReply()
                .withEmbedsOrNull(Collections.singleton(embed.build()))
                .withComponentsOrNull(menu.getComponents())
                .then())
            .then(event.getClient().on(ComponentInteractionEvent.class)
                .filter((c) -> c.getInteraction().getUser().getId().equals(event.getInteraction().getUser().getId()))
                .timeout(Duration.ofSeconds(360))
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .flatMap((c) -> processEvent(c, menu, embed.build()))
                .takeWhile((c) -> c)
                .then())
            ;
    }

}
