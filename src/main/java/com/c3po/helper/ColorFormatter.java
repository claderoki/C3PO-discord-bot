package com.c3po.helper;

import discord4j.rest.util.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColorFormatter {
    private static final Map<String, Color> colorMapping = Map.ofEntries(
        Map.entry("WHITE", Color.WHITE),
        Map.entry("DISCORD_WHITE",Color.DISCORD_WHITE),
        Map.entry("LIGHT_GRAY", Color.LIGHT_GRAY),
        Map.entry("GRAY", Color.GRAY),
        Map.entry("DARK_GRAY", Color.DARK_GRAY),
        Map.entry("BLACK", Color.BLACK),
        Map.entry("DISCORD_BLACK", Color.DISCORD_BLACK),
        Map.entry("RED", Color.RED),
        Map.entry("PINK", Color.PINK),
        Map.entry("ORANGE", Color.ORANGE),
        Map.entry("YELLOW", Color.YELLOW),
        Map.entry("GREEN", Color.GREEN),
        Map.entry("MAGENTA", Color.MAGENTA),
        Map.entry("CYAN", Color.CYAN),
        Map.entry("BLUE", Color.BLUE),
        Map.entry("LIGHT_SEA_GREEN", Color.LIGHT_SEA_GREEN),
        Map.entry("MEDIUM_SEA_GREEN", Color.MEDIUM_SEA_GREEN),
        Map.entry("SUMMER_SKY", Color.SUMMER_SKY),
        Map.entry("DEEP_LILAC", Color.DEEP_LILAC),
        Map.entry("RUBY", Color.RUBY),
        Map.entry("MOON_YELLOW", Color.MOON_YELLOW),
        Map.entry("TAHITI_GOLD", Color.TAHITI_GOLD),
        Map.entry("CINNABAR", Color.CINNABAR),
        Map.entry("SUBMARINE", Color.SUBMARINE),
        Map.entry("HOKI", Color.HOKI),
        Map.entry("DEEP_SEA", Color.DEEP_SEA),
        Map.entry("SEA_GREEN", Color.SEA_GREEN),
        Map.entry("ENDEAVOUR", Color.ENDEAVOUR),
        Map.entry("VIVID_VIOLET", Color.VIVID_VIOLET),
        Map.entry("JAZZBERRY_JAM", Color.JAZZBERRY_JAM),
        Map.entry("DARK_GOLDENROD", Color.DARK_GOLDENROD),
        Map.entry("RUST", Color.RUST),
        Map.entry("BROWN", Color.BROWN),
        Map.entry("GRAY_CHATEAU", Color.GRAY_CHATEAU),
        Map.entry("BISMARK", Color.BISMARK)
    );

    public static Color parse(String rawColor) {
        Color color = colorMapping.get(rawColor.toUpperCase());
        if (color != null) {
            return color;
        }

        try {
            color = Color.of(java.awt.Color.decode(rawColor).getRGB());
        } catch (Exception e) {
            LogHelper.log(e);
            return Color.BLUE;
        }
        return color;
    }
}
