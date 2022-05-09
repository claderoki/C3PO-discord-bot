package com.c3po.core;

import com.c3po.core.command.CommandManager;
import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.ConfigurationLoader;
import com.c3po.helper.environment.Mode;
import com.c3po.listener.CommandListener;
import com.c3po.listener.EventListener;
import com.c3po.listener.MessageCreateListener;
import discord4j.common.ReactorResources;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.presence.ClientPresence;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class C3PO {
    private final Mode mode;
    private final CommandManager commandManager;

    public C3PO(Mode mode) {
        this.mode = mode;
        commandManager = new CommandManager();
    }

    public void run() {
        Configuration config = Configuration.instance();

        final DiscordClient client = DiscordClientBuilder.create(config.getToken())
            .setReactorResources(getReactorResources())
            .build();

        client.gateway()
        .setInitialPresence(c -> ClientPresence.invisible())
        .withGateway(this::setupGateway)
        .block();
    }

    private ReactorResources getReactorResources() {
        return ReactorResources.builder()
            // https://github.com/Discord4J/Discord4J/issues/1020
            .httpClient(HttpClient.create()
                .compress(true)
                .keepAlive(false)
                .followRedirect(true)
                .secure())
            .build();
    }

    private Mono<?> setupGateway(GatewayDiscordClient gateway) {
        commandManager.registerAll(gateway.getRestClient(), false);

        register(gateway, new CommandListener(commandManager));
        register(gateway, new MessageCreateListener());

        LogHelper.log("Bot started up.");
        return gateway.onDisconnect();
    }

    private <T extends Event> void register(GatewayDiscordClient gateway, EventListener<T> eventListener) {
        gateway.getEventDispatcher()
            .on(eventListener.getEventType())
            .flatMap(event -> eventListener.execute(event)
                .timeout(Duration.ofSeconds(60), Mono.error(new TimeoutException("TIMED OUT"))))
            .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
            .subscribe(null, (e) -> LogHelper.log(e, eventListener.getEventType().getName()))
        ;
    }
}
