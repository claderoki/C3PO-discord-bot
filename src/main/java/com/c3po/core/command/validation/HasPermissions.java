package com.c3po.core.command.validation;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Set;

@RequiredArgsConstructor
public class HasPermissions extends CommandValidation {
    private final Set<Permission> permissions;

    private Mono<Boolean> validateMember(Member member) {
        return member.getBasePermissions()
            .map(p -> p.containsAll(permissions));
    }

    protected Mono<Member> getMember(ChatInputInteractionEvent event) {
        return event.getInteraction().getMember()
            .map(Mono::just)
            .orElse(Mono.empty());
    }

    public final Mono<Boolean> validate(ChatInputInteractionEvent event) {
        return getMember(event)
            .flatMap(this::validateMember)
            .defaultIfEmpty(false);
    }

    public static HasPermissions admin() {
        return new HasPermissions(Set.of(Permission.ADMINISTRATOR));
    }
}
