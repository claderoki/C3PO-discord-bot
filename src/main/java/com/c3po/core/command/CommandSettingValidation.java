package com.c3po.core.command;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;

import reactor.core.publisher.Mono;

public class CommandSettingValidation {
    public static Mono<Boolean> validate(CommandSettings commandSettings, Member member, Snowflake guildId) {
        if (commandSettings == null) {
            return Mono.just(true);
        }
        if (commandSettings.isAdminOnly() && member != null) {
            return member.getBasePermissions().map(p-> p != null && p.contains(Permission.ADMINISTRATOR));
        }
        if (commandSettings.isGuildOnly() && guildId == null) {
            return Mono.just(false);
        }

        return Mono.just(true);
    }

    public static Mono<Boolean> validate(CommandSettings commandSettings, ChatInputInteractionEvent event) {
        return validate(commandSettings, event.getInteraction().getMember().orElse(null), event.getInteraction().getGuildId().orElse(null));
    }
}
