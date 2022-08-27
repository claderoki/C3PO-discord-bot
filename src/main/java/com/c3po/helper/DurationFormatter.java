package com.c3po.helper;

import java.time.Duration;

public class DurationFormatter {

    public static Duration parse(String from) {
        from = from.replace("and", "").replace(",", "");
        String[] values = from.split(" ");
        return Duration.ofSeconds(0);
    }

    public static String toString(Duration from) {
        return from.toString();
    }
}
