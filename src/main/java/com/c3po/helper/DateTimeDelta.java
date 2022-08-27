package com.c3po.helper;

import java.util.ArrayList;
import java.util.List;

public record DateTimeDelta(long years, long months, long days, long hours, long minutes, long seconds) {
    public static DateTimeDelta fromSeconds(long seconds) {
        long years = (seconds / 2592000) / 12;
        long months = (seconds / 2592000) % 30;
        long days = (seconds / 86400) % 30;
        long hours = (seconds / 3600) % 24;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return new DateTimeDelta(years, months, days, hours, minutes, seconds);
    }

    private void pushSingleValue(long value, String name, List<String> messages) {
        if (messages.size() < 2 && value > 0) {
            name = value == 1 ? name : name + "s";
            messages.add(value + " " + name);
        }
    }

    public String format() {
        List<String> messages = new ArrayList<>();
        pushSingleValue(years, "year", messages);
        pushSingleValue(months, "month", messages);
        pushSingleValue(days, "day", messages);
        pushSingleValue(hours, "hour", messages);
        pushSingleValue(minutes, "minute", messages);
        pushSingleValue(seconds, "second", messages);
        return String.join(" and ", messages);
    }
}

