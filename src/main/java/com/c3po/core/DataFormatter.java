package com.c3po.core;

import com.c3po.helper.DataType;
import com.c3po.helper.DateTimeHelper;

import java.time.LocalDateTime;

public class DataFormatter {
    public static String prettify(DataType type, String value) {
        if (value == null) {
            return "n/a";
        }

        if (type.equals(DataType.BOOLEAN)) {
            return value.equals("1") ? "yes" : "no";
        }

        return value;
    }

    public static Object parse(DataType type, String value) {
        return switch (type) {
            case INTEGER, CHANNEL, CATEGORY -> Long.parseLong(value);
            case STRING -> value;
            case BOOLEAN -> value.equals("1") || value.equalsIgnoreCase("true");
            case DATETIME -> LocalDateTime.parse(value, DateTimeHelper.DEFAULT_FORMATTER);
        };
    }
}
