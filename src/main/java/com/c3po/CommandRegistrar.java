package com.c3po;


import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.environment.ConfigurationLoader;
import com.c3po.helper.environment.Mode;
import com.c3po.helper.setting.Setting;
import com.c3po.helper.setting.SettingTransformer;
import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistrar {
    private final RestClient restClient;

    private static final String commandsFolderName = "commands/";

    public CommandRegistrar(RestClient restClient) {
        this.restClient = restClient;
    }

    private ApplicationCommandRequest combineCommands(ApplicationCommandRequest first, ApplicationCommandRequest second) {
        ImmutableApplicationCommandRequest.Builder builder = ApplicationCommandRequest.builder();
        builder.from(first);

        for(ApplicationCommandOptionData option: second.options().get()) {
            builder.addOption(option);
        }

        return builder.build();
    }

    protected void registerCommands(List<String> fileNames) throws IOException {
        final JacksonResources d4jMapper = JacksonResources.create();

        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        Map<String, ApplicationCommandRequest> commands = new HashMap<>();
        for (Map.Entry<String, HashMap<String, Setting>> entrySet: SettingRepository.db().getAllSettings().entrySet()) {
            commands.put(entrySet.getKey(), SettingTransformer.toCommand(entrySet.getKey(), entrySet.getValue().values()));
        }

        for (String json: getCommandsJson(fileNames)) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper().readValue(json, ApplicationCommandRequest.class);
            ApplicationCommandRequest existingCommand = commands.get(request.name());
            if (existingCommand != null) {
                commands.put(existingCommand.name(), combineCommands(request, existingCommand));
            } else {
                commands.put(request.name(), request);
            }
        }

        if (ConfigurationLoader.instance().getMode().equals(Mode.DEVELOPMENT)) {
            //729843647347949638
            Long[] guildIds = {
                729843647347949638L,
//                761624318291476482L,
//                944339782002163732L,
            };
            for (Long guildId: guildIds) {
                applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands.values().stream().toList())
                    .doOnNext(cmd -> System.out.println("Successfully registered Global Command " + cmd.name()))
                    .doOnError(e -> System.out.println("Failed to register global commands" + e.getMessage()))
                    .subscribe();
            }
        } else {
            applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands.values().stream().toList()).subscribe();
        }
    }

    private static List<String> getCommandsJson(List<String> fileNames) throws IOException {
        URL url = CommandRegistrar.class.getClassLoader().getResource(commandsFolderName);
        Objects.requireNonNull(url, commandsFolderName + " could not be found");
        List<String> list = new ArrayList<>();
        for (String file : fileNames) {
            String resourceFileAsString = getResourceFileAsString(commandsFolderName + file);
            list.add(Objects.requireNonNull(resourceFileAsString, "Command file not found: " + file));
        }
        return list;
    }

    /**
     * Gets a specific resource file as String
     *
     * @param fileName The file path omitting "resources/"
     * @return The contents of the file as a String, otherwise throws an exception
     */
    private static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(fileName)) {
            if (resourceAsStream == null) return null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}