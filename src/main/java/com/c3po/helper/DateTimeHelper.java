package com.c3po.helper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime now() {
        return offsetNow().toLocalDateTime();
    }

    public static OffsetDateTime offsetNow() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

}
