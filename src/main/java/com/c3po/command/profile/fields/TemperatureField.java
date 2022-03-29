package com.c3po.command.profile.fields;

import com.c3po.core.openweatherapi.responses.Temperature;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.Map;

public class TemperatureField extends ProfileField<Temperature> {
    private final static Map<String, String> emojiMap = Map.ofEntries(
        Map.entry("Rain", "🌧"),
        Map.entry("Snow", "🌨"),
        Map.entry("Clear", "☀"),
        Map.entry("Clouds", "☁"),
        Map.entry("Thunderstorm", "⛈"),
        Map.entry("Fog", "🌫️"),
        Map.entry("Smoke", "💨"),
        Map.entry("Drizzle", "🌧"),
        Map.entry("Mist", "🌫️"),
        Map.entry("Haze", "🌫️")
    );

    public TemperatureField(Temperature value) {
        super(value);
    }

    @Override
    public ReactionEmoji getEmoji() {
        return ReactionEmoji.unicode(emojiMap.getOrDefault(value.getWeatherStatus(), ""));
    }

    @Override
    public String getValue() {
        return value.getTemperature() + "°C";
    }
}
