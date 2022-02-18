package com.c3po.command.guildrewards;

import com.c3po.command.Command;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.HashMap;

public abstract class GuildRewardsGroup extends Command {

    abstract String getSettingKey();

    String getValueParameter() {
        return null;
    }

    String getValueFromEvent(ChatInputInteractionEvent event) {
        String param = getValueParameter();

        if (param == null) {
            return null;
        }

        ApplicationCommandInteractionOptionValue value = InteractionHelper.getOptionValue(event, param);
        if (value == null) {
            return null;
        }
        return parseValue(value.getRaw());
    }

    protected Mono<Void> handleSetting(ChatInputInteractionEvent event) throws SQLException {
        long guildId = event.getInteraction().getGuildId().get().asLong();
        String value = getValueFromEvent(event);
        if (value == null && getValueParameter() != null) {
            return Mono.empty();
        }

        HashMap<String, Setting> settings = SettingCache.getSettings(getCategory());
        HashMap<Integer, SettingValue> values = SettingValueLoader.load(SettingScopeTarget.guild(guildId), getCategory());

        Setting setting = settings.get(getSettingKey());
        if (setting != null) {
            SettingValue settingValue = values.get(setting.getId());
            setValue(settingValue, value);
            return event.reply().withContent("Value changed from to " + settingValue.getValue() + " (was "+ settingValue.getOriginalValue() +")").then();
        }

        return Mono.empty();
    }

    protected void setValue(SettingValue settingValue, String value) {
        settingValue.setValue(value);
    }

    protected String getCategory() {
        return "guildrewards";
    }

    protected String parseValue(String value) {
        return value;
    }

    @Override
    public boolean validate(ChatInputInteractionEvent event) {
        return event.getInteraction().getGuildId().isPresent();
    }
}
