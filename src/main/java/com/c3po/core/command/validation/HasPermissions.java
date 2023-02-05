package com.c3po.core.command.validation;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

import java.util.Set;

public abstract class HasPermissions extends CommandValidation {
    protected abstract Set<Permission> getRequiredPermissions();

    private Mono<Boolean> validateMember(Member member) {
        return member.getBasePermissions()
            .map(p -> p.containsAll(getRequiredPermissions()));
    }

    public final Mono<Boolean> validate(ChatInputInteractionEvent event) {
        return event.getInteraction().getMember()
            .map(this::validateMember)
            .orElse(Mono.just(true));
    }
}
