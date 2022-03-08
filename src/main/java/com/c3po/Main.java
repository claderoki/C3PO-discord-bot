package com.c3po;

import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.ConfigurationLoader;
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

    public static void main(String[] args) {
        try {
            Configuration config = ConfigurationLoader.instance();

            ReactorResources reactorResources = ReactorResources.builder()
                .timerTaskScheduler(Schedulers.newParallel("my-scheduler"))
                .blockingTaskScheduler(Schedulers.boundedElastic())
                .build();

            final GatewayDiscordClient client = DiscordClientBuilder.create(config.getToken()).setReactorResources(reactorResources).build()
                .gateway().setInitialPresence((c) -> ClientPresence.invisible())
                .login()
                .block();

            List<String> commands = List.of("milkyway.json");
            assert client != null;
            new CommandRegistrar(client.getRestClient()).registerCommands(commands);
            client.on(MessageCreateEvent.class, MessageCreateListener::handle).subscribe();
            client.on(ChatInputInteractionEvent.class, CommandListener::handle)
                    .then(client.onDisconnect())
                    .block();
        } catch (Exception e) {
            LogHelper.logException(e);
            System.exit(0);
        }
    }
}
