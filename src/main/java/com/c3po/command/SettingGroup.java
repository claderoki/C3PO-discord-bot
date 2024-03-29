package com.c3po.command;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.DataFormatter;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.option.CommandOption;
import com.c3po.core.command.option.OptionContainer;
import com.c3po.core.command.validation.*;
import com.c3po.core.property.PropertyValue;
import com.c3po.core.setting.CategoryCacheKeyFactory;
import com.c3po.core.setting.Setting;
import com.c3po.core.setting.SettingCategory;
import com.c3po.core.setting.validation.*;
import com.c3po.helper.DataType;
import com.c3po.helper.EventHelper;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.SettingGroupCacheKey;
import com.c3po.service.SettingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.*;

public class SettingGroup {
    @Autowired
    private SettingService settingService;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private SettingValidationCache settingValidationCache;

    private final String settingKey;
    private final SettingCategory category;
    private final String settingParam;
    private Integer settingId;

    public SettingGroup(SettingCategory category, String settingKey) {
        this.category = category;
        this.settingKey = settingKey;
        this.settingParam = settingKey.replace("_id", "");
    }

    protected String getValueFromEvent(ChatInputInteractionEvent event) {
        OptionContainer options = EventHelper.getOptionContainer(event);
        CommandOption<?> value = options.get(settingParam);
        if (value == null) {
            return null;
        }
        return value.getRaw().getRaw();
    }

    protected EmbedCreateSpec createEmbedFor(PropertyValue settingValue) {
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

    public static List<CommandValidation> scopeToValidations(Scope scope) {
        return switch (scope) {
            case GUILD -> List.of(new GuildOnly(), HasPermissions.admin());
            case USER -> List.of();
            case MEMBER -> List.of(new GuildOnly());
        };
    }

    public static ScopeTarget scopeToTarget(Scope scope, ChatInputInteractionEvent event) {
        return switch (scope) {
            case GUILD -> ScopeTarget.guild(event.getInteraction().getGuildId().orElseThrow().asLong());
            case USER -> ScopeTarget.user(event.getInteraction().getUser().getId().asLong());
            case MEMBER -> ScopeTarget.member(
                    event.getInteraction().getUser().getId().asLong(),
                    event.getInteraction().getGuildId().orElseThrow().asLong()
            );
        };
    }

    private Mono<Void> handleValue(Setting setting, ChatInputInteractionEvent event, String value) {
        ScopeTarget target = scopeToTarget(setting.getScope(), event);
        ArrayList<Integer> requiredSettings = new ArrayList<>();
        ArrayList<SettingValidation> validations = settingValidationCache.get().get(settingId);
        requiredSettings.add(settingId);
        requiredSettings.addAll(getRequiredSettings(validations));
        HashMap<Integer, PropertyValue> settingValues = settingRepository.getHydratedPropertyValues(target, category.getType(), requiredSettings);
        PropertyValue settingValue = settingValues.get(settingId);
        setValue(settingValue, value);
        if (validations != null) {
            SettingValidator settingValidator = new SettingValidator(validations, settingValues);
            SettingValidationResult result = settingValidator.validate();
            ArrayList<String> errors = result.getErrors();
            if (!errors.isEmpty()) {
                return event.reply().withContent("Error(s): " + String.join("\n", errors)).then();
            }
        }
        settingRepository.save(settingValue);
        if (settingValue.changed()) {
            SettingGroupCacheKey<?> cacheKey = CategoryCacheKeyFactory.create(category, target);
            CacheManager.get().remove(cacheKey);
        }

        return event.reply().withEmbeds(createEmbedFor(settingValue));
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) {
        this.settingId = settingService.getId(category.getType(), settingKey);
        Setting setting = settingService.getSetting(settingId);
        return new CommandValidator().validate(event, scopeToValidations(setting.getScope()))
            .filter(v -> v)
            .map(c -> Optional.ofNullable(getValueFromEvent(event)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(v -> handleValue(setting, event, v));
    }

    protected void setValue(PropertyValue settingValue, String value) {
        settingValue.setValue(parseValue(settingValue, value));
    }

    protected String parseValue(PropertyValue settingValue, String value) {
        if (settingValue.getType().equals(DataType.BOOLEAN)) {
            return value.equals("true") ? "1" : "0";
        }
        return value;
    }

}
