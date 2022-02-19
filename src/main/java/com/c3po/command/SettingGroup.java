package com.c3po.command;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.DataType;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.DataFormatter;
import com.c3po.helper.setting.SettingScopeTarget;
import com.c3po.helper.setting.SettingValue;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@AllArgsConstructor
public class SettingGroup {
    private String category;
    private String settingKey;

    protected String getValueFromEvent(ChatInputInteractionEvent event) {
        String param = settingKey.replace("_id", "");
        ApplicationCommandInteractionOptionValue value = InteractionHelper.getOptionValue(event, param);
        if (value == null) {
            return null;
        }
        return value.getRaw();
    }

    protected EmbedCreateSpec createEmbedFor(SettingValue settingValue) {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                .color(Color.of(242, 180, 37))
        ;

        if (settingValue.changed()) {
            builder.description("Changed to `%s` (was `%s`)".formatted(
                    DataFormatter.prettify(settingValue.getType(), settingValue.getValue()),
                    DataFormatter.prettify(settingValue.getType(), settingValue.getOriginalValue())
            ));
        } else {
               builder.description("Remained unchanged.");
        }
        return builder.build();
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws SQLException {
        long guildId = event.getInteraction().getGuildId().orElseThrow().asLong();
        String value = getValueFromEvent(event);
        if (value == null) {
            return Mono.empty();
        }
        SettingScopeTarget target = SettingScopeTarget.guild(guildId);
        ArrayList<String> requiredSettings = new ArrayList<>();
        requiredSettings.add(settingKey);
        HashMap<String, SettingValue> settingValues = SettingRepository.db().getHydratedSettingValues(target, category, requiredSettings);
        SettingValue settingValue = settingValues.get(settingKey);
        setValue(settingValue, value);
        SettingRepository.db().save(settingValue);
        return event.reply().withEmbeds(createEmbedFor(settingValue)).then();
    }

    protected void setValue(SettingValue settingValue, String value) {
        settingValue.setValue(parseValue(settingValue, value));
    }

    protected String parseValue(SettingValue settingValue, String value) {
        if (settingValue.getType().equals(DataType.BOOLEAN)) {
            return value.equals("true") ? "1" : "0";
        }
        return value;
    }

}