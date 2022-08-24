package com.c3po.ui.input.base;

import com.c3po.helper.Unicode;
import com.c3po.ui.Toast;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.core.spec.InteractionReplyEditMono;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class MenuManager {
    private final Menu menu;
    private final Replier replier;

    public MenuManager(Menu menu) {
        this(menu, new Replier(menu.getContext().getEvent()));
    }

    @SuppressWarnings("unchecked")
    private Mono<Boolean> processEvent(ComponentInteractionEvent event) {
        MenuOption<?, ComponentInteractionEvent, ?> option = (MenuOption<?, ComponentInteractionEvent, ?>) menu.matchOption(event.getCustomId());
        if (option == null || !option.isAllowed(event)) {
            return Mono.just(true);
        }
        return option.execute(event)
            .then(Mono.defer(() -> afterProcess(option)))
            .then(Mono.just(menu.shouldContinue() && option.shouldContinue()));
    }

    private Mono<Void> afterProcess(MenuOption<?, ComponentInteractionEvent, ?> option) {
        menu.incrementOptionsHandled();
        if (option.shouldContinue()) {
            return sendMessage().then();
        }
        return Mono.empty();
    }

    private InteractionApplicationCommandCallbackReplyMono reply(InteractionApplicationCommandCallbackReplyMono replyMono) {
        EmbedCreateSpec embed = menu.getEmbed();
        if (embed != null) {
            replyMono = replyMono.withEmbeds(embed);
        } else {
            replyMono = replyMono.withContent(Unicode.EMPTY);
        }
        return replyMono.withComponents(menu.getComponents());
    }

    private InteractionReplyEditMono editReply(InteractionReplyEditMono editMono) {
        List<LayoutComponent> components = menu.getComponents();
        EmbedCreateSpec embed = menu.getEmbed();
        return editMono.withEmbedsOrNull(embed == null ? null : Collections.singleton(embed))
            .withComponentsOrNull(menu.shouldContinue() ? components : null);
    }

    private Mono<Message> sendMessage() {
        return replier.replyOrEdit(this::reply, this::editReply);
    }

    public Mono<Menu> waitFor() {
        return sendMessage().then(
            menu.getContext().getEvent().getClient().on(ComponentInteractionEvent.class)
                .filter(menu::isAllowed)
                .timeout(menu.getTimeout())
                .flatMap(this::processEvent)
                .takeWhile(c -> c)
                .then(Mono.just(menu))
                .onErrorResume(TimeoutException.class, ignore ->
                    menu.getContext().sendToast(Toast.builder().message("Menu timed out.").build())
                        .then(Mono.just(menu)))
        );
    }
}
