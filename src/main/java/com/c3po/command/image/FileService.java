package com.c3po.command.image;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.MessageCreateSpec;
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;

class FileService {
    private final GatewayDiscordClient client;

    private @Nullable PrivateChannel defaultChannel;

    FileService(GatewayDiscordClient client) {
        this.client = client;
    }

    private Mono<@NonNull PrivateChannel> getDefaultChannel() {
        if (defaultChannel == null) {
            return client.getUserById(Snowflake.of(771781840012705792L))
                .flatMap(User::getPrivateChannel)
                .doOnSuccess(c -> defaultChannel = c);
        }
        return Mono.just(defaultChannel);
    }

    private Mono<InputStream> externalPathToStream(String path) {
        return HttpClient.create().get().uri(path).responseSingle((r, bytes) -> bytes.asInputStream());
    }

    public Mono<String> store(String path) {
        Mono<InputStream> streamMono;
        if (path.startsWith("http")) {
            streamMono = externalPathToStream(path);
        } else {
            streamMono = Mono.empty();
        }
        return getDefaultChannel()
            .flatMap(c -> streamMono.flatMap(s -> c.createMessage(MessageCreateSpec.builder()
                .addFile("file.png", s)
                .build())))
            .map(m -> m.getAttachments().get(0).getUrl());
    }
}