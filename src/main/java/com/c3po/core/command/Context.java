package com.c3po.core.command;

import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.option.OptionContainer;
import com.c3po.helper.EventHelper;
import com.c3po.ui.Toast;
import com.c3po.ui.ToastType;
import com.c3po.ui.input.base.Interactor;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Getter
public class Context {
    private final ChatInputInteractionEvent event;
    private final OptionContainer options;
    private final HashMap<Scope, ScopeTarget> targets = new HashMap<>();
    private final Interactor interactor;

    public Context(ChatInputInteractionEvent event) {
        this.event = event;
        this.options = EventHelper.getOptionContainer(event);
        this.interactor = new Interactor(event);
    }

    private ScopeTarget getUncachedTarget(Scope scope) {
        return switch (scope) {
            case GUILD -> ScopeTarget.guild(event.getInteraction().getGuildId().orElseThrow().asLong());
            case USER -> ScopeTarget.user(event.getInteraction().getUser().getId().asLong());
            case MEMBER -> ScopeTarget.member(
                event.getInteraction().getMember().orElseThrow().getId().asLong(),
                event.getInteraction().getGuildId().orElseThrow().asLong()
            );
        };
    }

    public ScopeTarget getTarget(Scope scope) {
        return targets.computeIfAbsent(scope, c -> getUncachedTarget(scope));
    }

    public ScopeTarget getTarget() {
        if (event.getInteraction().getGuildId().isPresent()) {
            return getTarget(Scope.MEMBER);
        } else {
            return getTarget(Scope.USER);
        }
    }

    private EmbedCreateSpec.Builder getEmbedFor(ToastType type) {
        return switch (type) {
            case ERROR -> EmbedCreateSpec.builder().color(Color.RED);
            case WARNING -> EmbedCreateSpec.builder().color(Color.ORANGE);
            case NOTICE -> EmbedCreateSpec.builder().color(Color.BLUE);
        };
    }

    public Mono<Void> sendToast(Toast toast) {
        return event.createFollowup()
            .withEmbeds(getEmbedFor(toast.getType()).description(toast.getMessage()).build())
            .flatMap(m -> {
                if (toast.getRemoveAfter() != null) {
                    Mono.delay(toast.getRemoveAfter()).then(m.delete()).subscribe();
                }
                return Mono.empty();
            }
        );
    }

}
