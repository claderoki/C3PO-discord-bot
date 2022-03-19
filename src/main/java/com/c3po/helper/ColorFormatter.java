package com.c3po.helper;

import discord4j.rest.util.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorFormatter {
    private static final Map<String, Color> colorMapping = new HashMap<>();

    static {
        colorMapping.put("WHITE", Color.WHITE);
        colorMapping.put("DISCORD_WHITE", Color.DISCORD_WHITE);
        colorMapping.put("LIGHT_GRAY", Color.LIGHT_GRAY);
        colorMapping.put("GRAY", Color.GRAY);
        colorMapping.put("DARK_GRAY", Color.DARK_GRAY);
        colorMapping.put("BLACK", Color.BLACK);
        colorMapping.put("DISCORD_BLACK", Color.DISCORD_BLACK);
        colorMapping.put("RED", Color.RED);
        colorMapping.put("PINK", Color.PINK);
        colorMapping.put("ORANGE", Color.ORANGE);
        colorMapping.put("YELLOW", Color.YELLOW);
        colorMapping.put("GREEN", Color.GREEN);
        colorMapping.put("MAGENTA", Color.MAGENTA);
        colorMapping.put("CYAN", Color.CYAN);
        colorMapping.put("BLUE", Color.BLUE);
        colorMapping.put("LIGHT_SEA_GREEN", Color.LIGHT_SEA_GREEN);
        colorMapping.put("MEDIUM_SEA_GREEN", Color.MEDIUM_SEA_GREEN);
        colorMapping.put("SUMMER_SKY", Color.SUMMER_SKY);
        colorMapping.put("DEEP_LILAC", Color.DEEP_LILAC);
        colorMapping.put("RUBY", Color.RUBY);
        colorMapping.put("MOON_YELLOW", Color.MOON_YELLOW);
        colorMapping.put("TAHITI_GOLD", Color.TAHITI_GOLD);
        colorMapping.put("CINNABAR", Color.CINNABAR);
        colorMapping.put("SUBMARINE", Color.SUBMARINE);
        colorMapping.put("HOKI", Color.HOKI);
        colorMapping.put("DEEP_SEA", Color.DEEP_SEA);
        colorMapping.put("SEA_GREEN", Color.SEA_GREEN);
        colorMapping.put("ENDEAVOUR", Color.ENDEAVOUR);
        colorMapping.put("VIVID_VIOLET", Color.VIVID_VIOLET);
        colorMapping.put("JAZZBERRY_JAM", Color.JAZZBERRY_JAM);
        colorMapping.put("DARK_GOLDENROD", Color.DARK_GOLDENROD);
        colorMapping.put("RUST", Color.RUST);
        colorMapping.put("BROWN", Color.BROWN);
        colorMapping.put("GRAY_CHATEAU", Color.GRAY_CHATEAU);
        colorMapping.put("BISMARK", Color.BISMARK);
    }

    public static Color parse(String rawColor) {
        if (rawColor.startsWith("#")) {
            var color = java.awt.Color.decode(rawColor);
            return Color.of(color.getRed(), color.getGreen(), color.getBlue());
        }

        Color color = colorMapping.get(rawColor.toUpperCase());
        if (color == null) {
            return Color.BLUE;
        }
        return color;
    }
}
