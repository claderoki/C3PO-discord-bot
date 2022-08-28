package com.c3po.listener;

import com.c3po.command.SettingInfo;
import com.c3po.core.DataFormatter;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.*;
import com.c3po.command.SettingGroup;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.property.PropertyValue;
import com.c3po.error.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.LogHelper;
import com.c3po.core.setting.*;
import com.c3po.helper.LogScope;
import com.c3po.service.SettingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class CommandListener implements EventListener<ChatInputInteractionEvent> {
    @Autowired
    private CommandManager commandManager;
    @Autowired
    private SettingService settingService;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private final static HashMap<String, HashMap<String, String>> settingMap = new HashMap<>();

    public static void addSettingGroup(String category, String optionName, String settingKey) {
        settingMap.computeIfAbsent(category, c -> new HashMap<>()).put(optionName, settingKey);
    }

    private static void appendOptionFullyQualifiedCommandName(ApplicationCommandInteractionOption option, StringBuilder builder) {
        switch (option.getType()) {
            case SUB_COMMAND -> builder.append(" ").append(option.getName());
            case SUB_COMMAND_GROUP -> {
                builder.append(" ").append(option.getName());
                for (ApplicationCommandInteractionOption op1 : option.getOptions()) {
                    appendOptionFullyQualifiedCommandName(op1, builder);
                }
            }
        }
    }

    private static String getFullyQualifiedCommandName(ChatInputInteractionEvent event) {
        StringBuilder base = new StringBuilder(event.getCommandName());
        for (ApplicationCommandInteractionOption option: event.getOptions()) {
            appendOptionFullyQualifiedCommandName(option, base);
        }
        return base.toString();
    }

    private String valuesToView(HashMap<Integer, PropertyValue> settingValues) {
        StringBuilder builder = new StringBuilder();
        for (PropertyValue settingValue: settingValues.values()) {
            String key = settingService.getCode(settingValue.getParentId());
            builder.append(key)
                .append("\t\t\t")
                .append(DataFormatter.prettify(settingValue.getType(), settingValue.getValue()))
                .append("\n");
        }
        return builder.toString();
    }

    private Mono<Void> executeSettingGroup(ChatInputInteractionEvent event, String category, String settingKey) {
        if (settingKey.equals(SettingTransformer.viewOptionName)) {
            ScopeTarget target = SettingGroup.scopeToTarget(Scope.GUILD, event);
            HashMap<Integer, PropertyValue> values = settingRepository.getHydratedPropertyValues(target, category);
            String content = valuesToView(values);
            return event.reply().withContent("```\n%s```".formatted(content));
        }
        SettingGroup settingGroup = new SettingGroup(category, settingKey);
        beanFactory.autowireBean(settingGroup);
        LogHelper.log("Setting group " + settingKey + " is being started", LogScope.DEVELOPMENT);
        Mono<Void> commandResult = settingGroup.handle(event);
        LogHelper.log("Setting group " + settingKey + " is finished", LogScope.DEVELOPMENT);
        return commandResult;
    }

    public Mono<Void> execute(ChatInputInteractionEvent event) throws RuntimeException {
        String fullName = getFullyQualifiedCommandName(event);
        SettingInfo settingInfo = commandManager.matchSettingInfo(fullName);
        if (settingInfo != null) {
            return executeSettingGroup(event, settingInfo.getCategory(), settingInfo.getSettingKey());
        }

        Command command = commandManager.matchCommand(fullName);
        if (command != null) {
            Context context = new Context(event);
            return CommandSettingValidation.validate(command.getSettings(), event).flatMap(valid -> {
                if (!valid) {
                    return Mono.empty();
                }
                return processCommand(command, context);
            });
        }
        return Mono.empty();
    }

    private Mono<Void> beforeCommand(BucketManager bucketManager, Command command) {
        LogHelper.log("Starting command " + command.getName());
        if (bucketManager != null) {
            bucketManager.before();
        }
        return Mono.empty();
    }

    private Mono<Void> afterCommand(BucketManager bucketManager, Command command) {
        LogHelper.log("Finishing command " + command.getName());
        if (bucketManager != null) {
            bucketManager.after();
        }
        return Mono.empty();
    }

    private Mono<Void> handleError(BucketManager bucketManager, Context context, Throwable exception) {
        if (bucketManager != null) {
            bucketManager.after();
        }
        if (exception instanceof PublicException publicException) {
            EmbedCreateSpec embed = EmbedHelper.error(publicException.getMessage()).build();
            return context.getReplier().replyOrEdit(c -> c.withEmbeds(embed),
                c -> c.withEmbedsOrNull(Collections.singleton(embed)))
                .then();
        }
        LogHelper.log(exception);
        return Mono.empty();
    }

    private Mono<Void> processCommand(Command command, Context context) {
        BucketManager bucketManager = command.getBucket().map(c -> new BucketManager(c, command, context)).orElse(null);
        if (bucketManager != null && !bucketManager.validate()) {
            LogHelper.log("BUCKET FAILED.");
            return Mono.empty();
        }
        try {
            return Mono.defer(() -> beforeCommand(bucketManager, command))
                .then(command.run(context))
                .then(Mono.defer(() -> afterCommand(bucketManager, command)))
                .onErrorResume(e -> handleError(bucketManager, context, e));
        } catch (Exception e) {
            return handleError(bucketManager, context, e);
        }
    }

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

}