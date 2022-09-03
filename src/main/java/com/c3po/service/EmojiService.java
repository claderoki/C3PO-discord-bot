package com.c3po.service;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
public class EmojiService {
    private final HashMap<String, GuildEmoji> emojis = new HashMap<>();

    private void cache(GuildEmoji guildEmoji) {
        emojis.putIfAbsent(guildEmoji.getName(), guildEmoji);
    }

    public Mono<Void> addToMapping(Guild guild) {
        return guild.getEmojis()
            .doOnEach(e -> cache(e.get()))
            .then();
    }

    public GuildEmoji get(String name) {
        return emojis.get(name);
    }

}
