package com.c3po.core.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.util.Permission;

import reactor.core.publisher.Mono;

public class CommandSettingValidation {
    public static Mono<Boolean> validate(CommandSettings commandSettings, ChatInputInteractionEvent event) {
        if (commandSettings == null) {
            return Mono.just(true);
        }
        if (commandSettings.isAdminOnly() && event.getInteraction().getMember().isPresent()) {
            return event.getInteraction().getMember().get().getBasePermissions().map(p-> p != null && p.contains(Permission.ADMINISTRATOR));
        }
        if (commandSettings.isGuildOnly() && event.getInteraction().getGuildId().isEmpty()) {
            return Mono.just(false);
        }

        return Mono.just(true);
    }
}
