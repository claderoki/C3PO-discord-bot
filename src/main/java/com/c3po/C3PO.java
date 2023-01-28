package com.c3po;

import com.c3po.core.command.CommandManager;
import com.c3po.core.task.TaskManager;
import com.c3po.helper.LogHelper;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.environment.Configuration;
import com.c3po.listener.CommandListener;
import com.c3po.listener.EventListener;
import com.c3po.listener.MessageCreateListener;
import com.c3po.listener.VoiceStateUpdateListener;
import com.c3po.processors.attribute.*;
import discord4j.common.ReactorResources;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@RequiredArgsConstructor
public class C3PO {
    private GatewayDiscordClient gateway;
    private final CommandListener commandListener;

    private final TaskManager taskManager;
    private final CommandManager commandManager;

    private final MessageCreateListener messageCreateListener;
    private final VoiceStateUpdateListener voiceStateUpdateListener;

    private final AttributePurger attributePurger;
    private final ActivityEnsurer activityEnsurer;
    private final ChannelPurger channelPurger;
    private final SafeInactiveReminder safeInactiveReminder;
    private final CacheClearer cacheClearer;

    @PostConstruct
    public void postConstruct() {
        CacheManager.set(new Cache());
        CacheManager.set("flags", new Cache());
        run();
    }

    public void run() {
        Configuration config = Configuration.instance();

        final DiscordClient client = DiscordClientBuilder.create(config.getToken())
            .setReactorResources(getReactorResources())
            .build();

        client.gateway()
            .setEnabledIntents(IntentSet.all())
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

    private List<Task> getTasks() {
        return List.of(
            attributePurger,
            activityEnsurer,
            channelPurger,
            safeInactiveReminder,
            cacheClearer
        );
    }

    private Mono<Void> setupGateway(GatewayDiscordClient gateway) {
        this.gateway = gateway;
        DiscordInfo.initiate(gateway);

        commandManager.registerAll(gateway.getRestClient(), false);

        register(commandListener);
        register(messageCreateListener);
        register(voiceStateUpdateListener);

        getTasks().forEach(taskManager::register);

        LogHelper.log("Bot started up.");
        return gateway.onDisconnect();
    }

    private <T extends Event> void register(EventListener<T> eventListener) {
        gateway.getEventDispatcher()
            .on(eventListener.getEventType())
            .flatMap(event -> eventListener.execute(event)
                .timeout(Duration.ofMinutes(30), Mono.error(new TimeoutException("TIMED OUT")))
                .onErrorResume(Throwable.class, e -> {
                    LogHelper.log(e, "event listener");
                    return Mono.empty();
                })
            )
            .subscribe()
        ;
    }
}
