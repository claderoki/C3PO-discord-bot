package com.c3po.helper;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;

public class EmbedHelper {
    public static final Color COLOR = Color.of(242, 180, 37);

    public static EmbedCreateSpec.Builder normal(String description) {
        return base()
            .description(description == null ? Possible.absent() : Possible.of(description));
    }

    public static EmbedCreateSpec.Builder base() {
        return EmbedCreateSpec.builder()
            .color(COLOR);
    }

    public static EmbedCreateSpec.Builder normal() {
        return normal(null);
    }

    public static EmbedCreateSpec.Builder error(String description) {
        return EmbedCreateSpec.builder()
            .color(Color.of(255, 0, 0))
            .description(description);
    }

    public static EmbedCreateSpec.Builder notice(String description) {
        return EmbedCreateSpec.builder()
            .color(Color.of(73, 132, 155))
            .author(Unicode.EMPTY, null, "https://media.discordapp.net/attachments/744172199770062899/765879281498587136/Blue_question_mark_icon.svg.png")
            .description(description);
    }

}
