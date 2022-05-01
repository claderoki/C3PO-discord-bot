package com.c3po.helper;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;

public class EmbedHelper {
    public static final Color COLOR = Color.of(242, 180, 37);

    public static EmbedCreateSpec.Builder normal(String description) {
        return EmbedCreateSpec.builder()
            .color(COLOR)
            .description(description == null ? Possible.absent() : Possible.of(description));
    }

    public static EmbedCreateSpec.Builder normal() {
        return normal(null);
    }

    public static EmbedCreateSpec.Builder error(String description) {
        return EmbedCreateSpec.builder()
            .color(Color.of(255, 0, 0))
            .description(description);
    }

}
