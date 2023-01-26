package com.c3po.core;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public class SimpleMessage {
    private String content;
    private EmbedCreateSpec embed;

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<EmbedCreateSpec> getEmbed() {
        return Optional.ofNullable(embed);
    }

    public Optional<List<EmbedCreateSpec>> getEmbeds() {
        if (embed == null) {
            return Optional.empty();
        }
        return Optional.of(List.of(embed));
    }
}
