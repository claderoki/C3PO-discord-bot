package com.c3po;

import com.c3po.helpers.environment.Configuration;
import com.c3po.helpers.environment.ConfigurationLoader;
import com.c3po.listeners.CommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Configuration config = ConfigurationLoader.instance();

            final GatewayDiscordClient client = DiscordClientBuilder.create(config.getToken()).build()
                    .login()
                    .block();

            List<String> commands = List.of("guildrewards.json");
            new CommandRegistrar(client.getRestClient()).registerCommands(commands);
            client.on(ChatInputInteractionEvent.class, CommandListener::handle)
                    .then(client.onDisconnect())
                    .block();
        } catch (Exception e) {
            System.exit(0);
        }
    }
}
