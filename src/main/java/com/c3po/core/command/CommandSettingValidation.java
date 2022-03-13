package com.c3po.core.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class CommandSettingValidation {
    public static boolean validate(CommandSettings commandSettings, ChatInputInteractionEvent event) {
        if (commandSettings == null) {
            return true;
        }
        if (commandSettings.isAdminOnly() && event.getInteraction().getMember().isPresent()) {
            PermissionSet permissions = event.getInteraction().getMember().get().getBasePermissions().block();
            if (permissions == null || !permissions.contains(Permission.ADMINISTRATOR)) {
                return false;
            }
        }
        if (commandSettings.isGuildOnly() && event.getInteraction().getGuildId().isEmpty()) {
            return false;
        }

        return true;
    }
}
