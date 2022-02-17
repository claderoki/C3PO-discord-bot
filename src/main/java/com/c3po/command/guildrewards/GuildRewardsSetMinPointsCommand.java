package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.Setting;
import com.c3po.helper.setting.SettingCache;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public class GuildRewardsSetMinPointsCommand extends GuildRewardsGroup {
    public String getName() {
        return getCategory() + " set minpoints";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws Exception {
        if (event.getInteraction().getGuildId().isEmpty()) {
            return Mono.empty();
        }
        long guildId = event.getInteraction().getGuildId().get().asLong();
        ApplicationCommandInteractionOptionValue value = InteractionHelper.getOptionValue(event, "points");

        HashMap<String, Setting> settings = SettingCache.getSettings(getCategory());
        HashMap<Integer, SettingValue> values = SettingCache.getValues(SettingScopeTarget.guild(guildId), getCategory());

        Setting setting = settings.get(getSettingKey());
        if (setting != null) {
            SettingValue settingValue = values.get(setting.getId());
            settingValue.setValue(value.getRaw());
        }

        return Mono.empty();
    }

    @Override
    String getSettingKey() {
        return "min_points_per_message";
    }
}
