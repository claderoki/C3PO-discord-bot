package com.c3po.helper;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class EmbedHelper {

    public static EmbedCreateSpec.Builder normal(String message) {
        return EmbedCreateSpec.builder()
            .color(Color.of(242, 180, 37))
            .description(message);
    }

}
