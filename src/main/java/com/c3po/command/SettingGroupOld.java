package com.c3po.command;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.DataFormatter;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SettingGroupOld extends Command {
    protected abstract String getCategory();

    protected abstract String getSettingKey();

    protected String getValueParameter() {
        return null;
    }

    protected String getValueFromEvent(ChatInputInteractionEvent event) {
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

    protected EmbedCreateSpec createEmbedFor(SettingValue settingValue) {
        return EmbedCreateSpec.builder()
                .color(Color.of(242, 180, 37))
                .description("Changed to `%s` (was `%s`)".formatted(
                        DataFormatter.prettify(settingValue.getType(), settingValue.getValue()),
                        DataFormatter.prettify(settingValue.getType(), settingValue.getOriginalValue())
                ))
                .build();
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws SQLException {
        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        String value = getValueFromEvent(event);
        if (value == null && getValueParameter() != null) {
            return Mono.empty();
        }
        SettingScopeTarget target = SettingScopeTarget.guild(guildId);
        ArrayList<String> requiredSettings = getRequiredSettings();
        requiredSettings.add(getSettingKey());
        HashMap<String, SettingValue> settingValues = SettingRepository.db().getHydratedSettingValues(target, getCategory(), requiredSettings);
        SettingValue settingValue = settingValues.get(getSettingKey());
        setValue(settingValue, value);
        SettingRepository.db().save(settingValue);
        return event.reply().withEmbeds(createEmbedFor(settingValue)).then();
    }

    protected void setValue(SettingValue settingValue, String value) {
        settingValue.setValue(value);
    }

    protected String parseValue(String value) {
        return value;
    }

}
