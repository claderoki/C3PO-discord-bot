package com.c3po.command;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.setting.SettingScope;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@AllArgsConstructor
public class SettingGroupSingle {
    private final String category;

    public Mono<Void> handle(ChatInputInteractionEvent event) {
        SettingScopeTarget target = SettingGroup.scopeToTarget(SettingScope.GUILD, event);
        HashMap<Integer, SettingValue> values = SettingRepository.db().getHydratedSettingValues(target, category);




        return Mono.empty();
    }

}
