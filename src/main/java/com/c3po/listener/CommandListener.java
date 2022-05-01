package com.c3po.listener;

import com.c3po.command.SettingInfo;
import com.c3po.command.milkyway.MilkywayCommandGroup;
import com.c3po.core.DataFormatter;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.*;
import com.c3po.command.SettingGroup;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.property.PropertyValue;
import com.c3po.errors.PublicException;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.EventHelper;
import com.c3po.helper.LogHelper;
import com.c3po.core.setting.*;
import com.c3po.helper.LogScope;
import com.c3po.service.SettingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Target;
import java.util.*;

public class CommandListener implements EventListener<ChatInputInteractionEvent> {

    private final static List<Command> commands = List.of(
        new MilkywayCommandGroup()
    );
    private final CommandManager commandManager;

    public CommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

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

    private static String valuesToView(HashMap<Integer, PropertyValue> settingValues) {
        StringBuilder builder = new StringBuilder();
        for (PropertyValue settingValue: settingValues.values()) {
            String key = SettingService.getCode(settingValue.getParentId());
            builder.append(key)
                .append("\t\t\t")
                .append(DataFormatter.prettify(settingValue.getType(), settingValue.getValue()))
                .append("\n");
        }
        return builder.toString();
    }

    private Mono<?> executeSettingGroup(ChatInputInteractionEvent event, String category, String settingKey) {
        if (settingKey.equals(SettingTransformer.viewOptionName)) {
            ScopeTarget target = SettingGroup.scopeToTarget(Scope.GUILD, event);
            HashMap<Integer, PropertyValue> values = SettingRepository.db().getHydratedPropertyValues(target, category);
            String content = valuesToView(values);
            return event.reply().withContent("```\n%s```".formatted(content));
        }
        SettingGroup settingGroup = new SettingGroup(category, settingKey);
        LogHelper.log("Setting group " + settingKey + " is being started", LogScope.DEVELOPMENT);
        Mono<?> commandResult = settingGroup.handle(event);
        LogHelper.log("Setting group " + settingKey + " is finished", LogScope.DEVELOPMENT);
        return commandResult;
    }

    public Mono<?> execute(ChatInputInteractionEvent event) throws RuntimeException {
        String fullName = getFullyQualifiedCommandName(event);
        SettingInfo settingInfo = commandManager.matchSettingInfo(fullName);
        if (settingInfo != null) {
            return executeSettingGroup(event, settingInfo.getCategory(), settingInfo.getSettingKey())
                .onErrorResume(this::handleError);
        }

        Command command = commandManager.matchCommand(fullName);
        if (command != null) {
            Context context = new Context(event);
            if (CommandSettingValidation.validate(command.getSettings(), event)) {
                return processCommand(command, context)
                    .onErrorResume(this::handleError);
            }
        }
        return Mono.empty();
    }

    private <T> Mono<T> handleError(Throwable e) {
        LogHelper.log(e);
        return Mono.empty();
    }

    private Mono<?> processCommand(Command command, Context context) {
        Optional<BucketManager> bucketManager = command.getBucket().map(c -> new BucketManager(c, command, context));
        Optional<Boolean> valid = bucketManager.map(BucketManager::validate);
        if (valid.isPresent() && !valid.get()) {
            LogHelper.log("BUCKET FAILED.");
            return Mono.empty();
        }

        try {
            bucketManager.ifPresent(BucketManager::before);
            Mono<?> commandResult = command.execute(context);
            bucketManager.ifPresent(BucketManager::after);
            return commandResult;
        } catch (Exception e) {
            bucketManager.ifPresent(BucketManager::after);
            if (e instanceof PublicException publicException) {
                EmbedCreateSpec embed = EmbedHelper.error(publicException.getMessage()).build();
                return context.getEvent().reply().withEmbeds(embed)
                    .onErrorResume((c) -> context.getEvent().editReply().withEmbedsOrNull(Collections.singleton(embed)).then());
            }
            LogHelper.log(e);
            return Mono.empty();
        }
    }

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

}