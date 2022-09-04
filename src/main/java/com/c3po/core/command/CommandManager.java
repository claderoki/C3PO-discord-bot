package com.c3po.core.command;

import com.c3po.command.SettingInfo;
import com.c3po.command.blackjack.BlackjackCommandGroup;
import com.c3po.command.hangman.HangmanCommandGroup;
import com.c3po.command.insecta.InsectaCommandGroup;
import com.c3po.command.milkyway.MilkywayCommandGroup;
import com.c3po.command.personalrole.PersonalRoleCommandGroup;
import com.c3po.command.pigeon.PigeonCommandGroup;
import com.c3po.command.profile.ProfileCommandGroup;
import com.c3po.command.snakeoil.SnakeOilCommandGroup;
import com.c3po.connection.repository.SettingRepository;
import com.c3po.core.setting.Setting;
import com.c3po.core.setting.SettingTransformer;
import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.Mode;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@NoArgsConstructor
@Component
public class CommandManager {
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private ProfileCommandGroup profileCommandGroup;
    @Autowired
    private MilkywayCommandGroup milkywayCommandGroup;
    @Autowired
    private PersonalRoleCommandGroup personalRoleCommandGroup;
    @Autowired
    private PigeonCommandGroup pigeonCommandGroup;
    @Autowired
    private SnakeOilCommandGroup snakeOilCommandGroup;
    @Autowired
    private HangmanCommandGroup hangmanCommandGroup;
    @Autowired
    private BlackjackCommandGroup blackjackCommandGroup;
    @Autowired
    private InsectaCommandGroup insectaCommandGroup;

    final HashMap<String, Command> commands = new HashMap<>();
    final HashMap<String, SettingInfo> settings = new HashMap<>();
    final Map<String, ApplicationCommandRequest> commandRequestList = new HashMap<>();

    private void registerCommands() {
        register(milkywayCommandGroup);
        register(personalRoleCommandGroup);
        register(profileCommandGroup);
        register(pigeonCommandGroup);
        register(snakeOilCommandGroup);
        register(hangmanCommandGroup);
        register(blackjackCommandGroup);
        register(insectaCommandGroup);
    }

    private void registerSettings() {
        for (Map.Entry<String, HashMap<String, Setting>> entrySet: settingRepository.getAllSettings().entrySet()) {
            register(entrySet.getKey(), entrySet.getValue().values());
        }
    }

    private String getCommandsHash() {
        String all = String.join("", commands.keySet().stream().sorted().toList())
            + String.join("", settings.keySet().stream().sorted().toList()).replace(" ", "");
        return String.valueOf(all.hashCode());
    }

    private boolean tryCreateFile(File file) {
        if (file.exists()) {
            return false;
        }
        try {
            return !file.createNewFile();
        } catch (IOException ex) {
            LogHelper.log(ex);
            return true;
        }
    }

    @SneakyThrows
    private String getPreviousCommandsHash() {
        File file = new File(".data/commandHash");
        if (tryCreateFile(file)) {
            return null;
        }

        String hash = null;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            hash = scanner.nextLine();
        }
        scanner.close();
        return hash;
    }

    @SneakyThrows
    private void saveCommandHash(String hash) {
        File file = new File(".data/commandHash");
        if (tryCreateFile(file)) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(hash.getBytes());
        fos.close();
    }

    public void registerAll(RestClient restClient, boolean clear) {
        if (!clear) {
            registerCommands();
            registerSettings();
        }

        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().blockOptional().orElseThrow();

        String currentHash = getCommandsHash();
        String previousHash = getPreviousCommandsHash();

        if (currentHash.equals(previousHash)) {
            return;
        }

        saveCommandHash(currentHash);

        List<ApplicationCommandRequest> requests = commandRequestList.values().stream().toList();
        if (Configuration.instance().getMode().equals(Mode.DEVELOPMENT)) {
            Long[] guildIds = {
                695416318681415790L,
                729843647347949638L,
                1013158959315701930L
            };
            for (Long guildId: guildIds) {
                applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, requests)
                    .doOnNext(cmd -> LogHelper.log("Successfully registered guild command " + cmd.name()))
                    .doOnError(e -> LogHelper.log("Failed to register guild commands: " + e.getMessage()))
                    .subscribe();
            }
        } else {
            applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, requests)
                .doOnNext(cmd -> LogHelper.log("Successfully registered global command " + cmd.name()))
                .doOnError(e -> LogHelper.log("Failed to register global commands: " + e.getMessage()))
                .subscribe();
        }
    }

    private ApplicationCommandRequest combineCommands(ApplicationCommandRequest first, ApplicationCommandRequest second) {
        ImmutableApplicationCommandRequest.Builder builder = ApplicationCommandRequest.builder();
        builder.from(first);

        for(ApplicationCommandOptionData option: second.options().get()) {
            builder.addOption(option);
        }

        return builder.build();
    }

    public void addCommandRequest(ApplicationCommandRequest request) {
        ApplicationCommandRequest existingCommand = commandRequestList.get(request.name());
        if (existingCommand != null) {
            commandRequestList.put(existingCommand.name(), combineCommands(request, existingCommand));
        } else {
            commandRequestList.put(request.name(), request);
        }
    }

    public void register(Command command) {
        addCommandRequest(command.asRequest());
        this.commands.put(command.getName(), command);
    }

    public void register(CommandGroup group) {
        addCommandRequest(group.asRequest());
        for(SubCommand command: group.getCommands()) {
            this.commands.put(command.getFullName(), command);
        }
    }

    public void register(String category, Collection<Setting> settings) {
        addCommandRequest(SettingTransformer.toCommand(category, settings, this));
    }

    public void register(String fullName, SettingInfo settingInfo) {
        settings.put(fullName, settingInfo);
    }

    public Command matchCommand(String fullName) {
        return commands.get(fullName);
    }

    public SettingInfo matchSettingInfo(String fullName) {
        return settings.get(fullName);
    }

}
