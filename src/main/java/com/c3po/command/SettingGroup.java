package com.c3po.command;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.DataType;
import com.c3po.helper.InteractionHelper;
import com.c3po.helper.setting.*;
import com.c3po.helper.setting.cache.SettingCache;
import com.c3po.helper.setting.cache.SettingValidationCache;
import com.c3po.helper.setting.cache.SettingValueCache;
import com.c3po.helper.setting.validation.SettingValidation;
import com.c3po.helper.setting.validation.SettingValidationResult;
import com.c3po.helper.setting.validation.SettingValidator;
import com.c3po.helper.setting.validation.ValueType;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SettingGroup {
    private final String category;
    private final String settingParam;
    private final Integer settingId;

    public SettingGroup(String category, String settingKey) {
        this.category = category;
        this.settingParam = settingKey.replace("_id", "");
        this.settingId = SettingCache.getId(category, settingKey);
    }

    protected String getValueFromEvent(ChatInputInteractionEvent event) {
        ApplicationCommandInteractionOptionValue value = InteractionHelper.getOptionValue(event, settingParam);
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

    private HashSet<Integer> getRequiredSettings(ArrayList<SettingValidation> validations) {
        HashSet<Integer> requiredSettings = new HashSet<>();
        if (validations == null) {
            return requiredSettings;
        }

        for (SettingValidation validation: validations) {
            if (validation.getValueType().equals(ValueType.SETTING)) {
                requiredSettings.add(Integer.parseInt(validation.getValue()));
            }
        }

        return requiredSettings;
    }

    public static CommandSettings scopeToSettings(SettingScope scope) {
        return switch (scope) {
            case GUILD -> CommandSettings.builder().guildOnly(true).adminOnly(true).build();
            case USER -> CommandSettings.builder().guildOnly(false).adminOnly(false).build();
            case MEMBER -> CommandSettings.builder().guildOnly(true).adminOnly(false).build();
        };
    }

    public static SettingScopeTarget scopeToTarget(SettingScope scope, ChatInputInteractionEvent event) {
        return switch (scope) {
            case GUILD ->SettingScopeTarget.guild(event.getInteraction().getGuildId().orElseThrow().asLong());
            case USER -> SettingScopeTarget.user(event.getInteraction().getUser().getId().asLong());
            case MEMBER -> SettingScopeTarget.member(
                    event.getInteraction().getUser().getId().asLong(),
                    event.getInteraction().getGuildId().orElseThrow().asLong()
            );
        };
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) throws SQLException {
        Setting setting = SettingCache.get(category).get(settingId);
        CommandSettings commandSettings = scopeToSettings(setting.getScope());
        if (!CommandSettingValidation.validate(commandSettings, event)) {
            return Mono.empty();
        }

        String value = getValueFromEvent(event);
        if (value == null) {
            return Mono.empty();
        }

        SettingScopeTarget target = scopeToTarget(setting.getScope(), event);
        ArrayList<Integer> requiredSettings = new ArrayList<>();
        ArrayList<SettingValidation> validations = SettingValidationCache.get().get(settingId);
        requiredSettings.add(settingId);
        requiredSettings.addAll(getRequiredSettings(validations));
        HashMap<Integer, SettingValue> settingValues = SettingRepository.db().getHydratedSettingValues(target, category, requiredSettings);
        SettingValue settingValue = settingValues.get(settingId);
        setValue(settingValue, value);
        if (validations != null) {
            SettingValidator settingValidator = new SettingValidator(validations, settingValues);
            SettingValidationResult result = settingValidator.validate();
            ArrayList<String> errors = result.getErrors();
            if (!errors.isEmpty()) {
                return event.reply().withContent("Error(s): " + String.join("\n", errors));
            }
        }
        SettingRepository.db().save(settingValue);
        if (settingValue.changed()) {
            SettingValueCache.clear(target, category);
        }
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
