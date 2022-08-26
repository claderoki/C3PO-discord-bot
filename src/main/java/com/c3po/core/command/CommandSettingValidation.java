package com.c3po.core.command;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;

import reactor.core.publisher.Mono;

import java.util.Optional;

public class CommandSettingValidation {
    public static Mono<Boolean> validate(CommandSettings commandSettings, Optional<Member> member, Optional<Snowflake> guildId) {
        if (commandSettings == null) {
            return Mono.just(true);
        }
        if (commandSettings.isAdminOnly() && member.isPresent()) {
            return member.get().getBasePermissions().map(p-> p != null && p.contains(Permission.ADMINISTRATOR));
        }
        if (commandSettings.isGuildOnly() && guildId.isEmpty()) {
            return Mono.just(false);
        }

        return Mono.just(true);
    }

    public static Mono<Boolean> validate(CommandSettings commandSettings, ChatInputInteractionEvent event) {
        return validate(commandSettings, event.getInteraction().getMember(), event.getInteraction().getGuildId());
    }
}
