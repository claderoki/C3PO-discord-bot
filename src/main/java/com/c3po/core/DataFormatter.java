package com.c3po.core;

import com.c3po.helper.DataType;
import com.c3po.helper.DateTimeHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    public static String toRaw(Object value) {
        if (value instanceof Boolean v) {
            return v ? "1" : "0";
        }

        return value.toString();
    }

    public static String prettify(Object value) {
        return prettify(getDataType(value), toRaw(value));
    }

    public static DataType getDataType(Object value) {
        if (value instanceof Long || value instanceof Integer) {
            return DataType.INTEGER;
        } else if (value instanceof Boolean) {
            return DataType.BOOLEAN;
        }

        return DataType.STRING;
    }

    public static Object parse(DataType type, String value) {
        return switch (type) {
            case INTEGER, CHANNEL, CATEGORY, ROLE -> Long.parseLong(value);
            case STRING -> value;
            case BOOLEAN -> value.equals("1") || value.equalsIgnoreCase("true");
            case DATETIME -> LocalDateTime.parse(value, DateTimeHelper.DATETIME_FORMATTER);
            case DATE -> LocalDate.parse(value, DateTimeHelper.DATE_FORMATTER);
            case TIME -> LocalTime.parse(value, DateTimeHelper.TIME_FORMATTER);
        };
    }
}
