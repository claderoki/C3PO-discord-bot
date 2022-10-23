package com.c3po.helper;

import java.time.Duration;
import java.util.*;

public class DurationFormatter {

    private static List<String> getValues(String from) {
        List<String> values = new ArrayList<>();
        String previous = null;
        for(String word: from.split(" ")) {
            if (previous == null) {
                previous = word;
            } else {
                values.add(previous + " " + word);
                previous = null;
            }
        }
        return values;
    }

    private static String preParse(String from) {
        return from
            .replace("and", "")
            .replace(",", "")
            ;
    }
    private static Duration parseSingle(String from) {
        String[] words = from.split(" ");
        long count = Long.parseLong(words[0]);
        return switch (words[1]) {
            case "day", "days" -> Duration.ofDays(count);
            case "minute", "minutes" -> Duration.ofMinutes(count);
            case "hour", "hours" -> Duration.ofHours(count);
            case "second", "seconds" -> Duration.ofSeconds(count);
            default -> null;
        };
    }

    public static Duration parse(String from) {
        from = preParse(from);
        long seconds = getValues(from)
            .stream()
            .map(DurationFormatter::parseSingle)
            .filter(Objects::nonNull)
            .mapToLong(Duration::getSeconds)
            .sum();
        return Duration.ofSeconds(seconds);
    }

    public static String toString(Duration from) {
        return from.toString();
    }
}
