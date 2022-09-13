package com.c3po.ui.input.base;

import com.c3po.helper.Unicode;
import com.c3po.ui.Toast;
import discord4j.core.GatewayDiscordClient;
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
public class MenuManager<M extends Menu> {
    private final M menu;
    private final Interactor interactor;

    public MenuManager(M menu) {
        this(menu, new Interactor(menu.getContext().getEvent()));
    }

    @SuppressWarnings("unchecked")
    private Mono<Boolean> processEvent(ComponentInteractionEvent event) {
        MenuOption<?, ComponentInteractionEvent, ?> option = (MenuOption<?, ComponentInteractionEvent, ?>) menu.matchOption(event.getCustomId());
        if (option == null || !option.isAllowed(event)) {
            return Mono.just(true);
        }
        return option.execute(event)
            .then(Mono.defer(() -> afterProcess(option)))
            .then(Mono.defer(() -> Mono.just(menu.shouldContinue() && option.shouldContinue())));
    }

    private Mono<Void> afterProcess(MenuOption<?, ComponentInteractionEvent, ?> option) {
        Mono<Void> mono = Mono.empty();
        if (option.shouldContinue()) {
            mono = mono.then(sendMessage()).then();
        }
        return mono.then(Mono.fromRunnable(menu::incrementOptionsHandled));
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
            .withComponentsOrNull(components);
    }

    private Mono<Message> sendMessage() {
        return interactor.replyOrEdit(this::reply, this::editReply);
    }

    public Mono<M> waitFor() {
        GatewayDiscordClient client = menu.getContext().getEvent().getClient();
        return sendMessage().then(
            client.on(ComponentInteractionEvent.class)
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
