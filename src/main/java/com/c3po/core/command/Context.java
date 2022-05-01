package com.c3po.core.command;

import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.option.OptionContainer;
import com.c3po.helper.EventHelper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Context {
    private final ChatInputInteractionEvent event;
    private final OptionContainer options;
    private final HashMap<Scope, ScopeTarget> targets = new HashMap<>();

    public Context(ChatInputInteractionEvent event) {
        this.event = event;
        this.options = EventHelper.getOptionContainer(event);
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

}
