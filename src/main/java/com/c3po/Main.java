package com.c3po;

import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.ConfigurationLoader;
import com.c3po.helper.environment.Mode;
import com.c3po.listener.CommandListener;
import com.c3po.listener.MessageCreateListener;
import discord4j.common.ReactorResources;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.ClientPresence;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class Main {

    private static void run(Mode mode) throws Exception {
        Configuration.initiate(mode);
        Configuration config = Configuration.instance();

        ReactorResources reactorResources = ReactorResources.builder()
            .timerTaskScheduler(Schedulers.newParallel("my-scheduler"))
            .blockingTaskScheduler(Schedulers.boundedElastic())
            .build();

        final GatewayDiscordClient client = DiscordClientBuilder.create(config.getToken()).setReactorResources(reactorResources).build()
            .gateway().setInitialPresence((c) -> ClientPresence.invisible())
            .login()
            .blockOptional().orElseThrow();

        List<String> commands = List.of("milkyway.json");
        new CommandRegistrar(client.getRestClient()).registerCommands(commands);
        client.on(MessageCreateEvent.class, MessageCreateListener::handle).subscribe();
        client.on(ChatInputInteractionEvent.class, CommandListener::handle)
            .then(client.onDisconnect())
            .block();
    }

    public static void main(String[] args) {
        try {
            Mode mode;
            if (args.length == 0) {
                mode = Mode.DEVELOPMENT;
            } else {
                mode = Mode.find(args[0]);
            }
            run(mode);
        } catch (Exception e) {
            LogHelper.logException(e);
            System.exit(0);
        }
    }
}
