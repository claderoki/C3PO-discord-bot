package com.c3po.command.image;

import com.c3po.DiscordInfo;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.MessageCreateSpec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileService {
    private final DiscordInfo discordInfo;

    private @Nullable PrivateChannel defaultChannel;

    private Mono<@NonNull PrivateChannel> getDefaultChannel() {
        if (defaultChannel == null) {
            return discordInfo.getClient().getUserById(Snowflake.of(771781840012705792L))
                .flatMap(User::getPrivateChannel)
                .doOnSuccess(c -> defaultChannel = c);
        }
        return Mono.just(defaultChannel);
    }

    private Mono<InputStream> externalPathToStream(String path) {
        return HttpClient.create().get().uri(path).responseSingle((r, bytes) -> bytes.asInputStream());
    }

    public Mono<String> store(String path, String filename) {
        Mono<InputStream> streamMono;
        if (path.startsWith("http")) {
            streamMono = externalPathToStream(path);
        } else {
            streamMono = Mono.empty();
        }
        return store(streamMono, filename);
    }

    public Mono<String> store(Mono<InputStream> streamMono, String filename) {
        return getDefaultChannel()
            .flatMap(c -> streamMono.flatMap(s -> c.createMessage(MessageCreateSpec.builder()
                .addFile(filename, s)
                .build())))
            .map(m -> m.getAttachments().get(0).getUrl());
    }

    public Mono<String> store(InputStream stream, String filename) {
        return store(Mono.just(stream), filename);
    }

}