package com.c3po.core.command.validation;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

import java.util.Set;

public class BotHasPermissions extends HasPermissions {
    public BotHasPermissions(Set<Permission> permissions) {
        super(permissions);
    }

    @Override
    protected Mono<Member> getMember(ChatInputInteractionEvent event) {
        return event.getInteraction().getGuild().flatMap(Guild::getSelfMember);
    }
}
