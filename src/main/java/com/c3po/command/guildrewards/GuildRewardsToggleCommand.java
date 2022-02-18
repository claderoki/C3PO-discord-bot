package com.c3po.command.guildrewards;

import com.c3po.helper.setting.SettingValue;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class GuildRewardsToggleCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " toggle";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        return handleSetting(event);
    }

    @Override
    protected void setValue(SettingValue settingValue, String value) {
        String newValue = settingValue.getValue().equals("0") ? "1" : "0";
        settingValue.setValue(newValue);
    }

    @Override
    String getSettingKey() {
        return "enabled";
    }
}
