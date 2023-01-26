package com.c3po.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public interface ValueParser {
    String optString(String key);

    private <F> F get(String key, Function<String, F> func) throws RuntimeException {
        String value = optString(key);
        if (value == null) {
            throw new RuntimeException(key + " was null but is required.");
        }
        return func.apply(value);
    }

    private <F> F getOr(String key, Function<String, F> func, F defaultValue) throws RuntimeException {
        String value = optString(key);
        if (value == null) {
            return defaultValue;
        }
        return func.apply(value);
    }

    private <F> F opt(String key, Function<String, F> func) throws RuntimeException {
        return getOr(key, func, null);
    }

    default String getString(String key) {
        return get(key, c -> c);
    }

    default String getStringOr(String key, String defaultValue) {
        return getOr(key, c -> c, defaultValue);
    }

    default int getInt(String key) {
        return get(key, Integer::parseInt);
    }

    default Integer optInt(String key) {
        return opt(key, Integer::parseInt);
    }

    default long getLong(String key) {
        return get(key, Long::parseLong);
    }

    default long getLongOr(String key, long defaultValue) {
        return getOr(key, Long::parseLong, defaultValue);
    }

    default int getIntOr(String key, int defaultValue) {
        return getOr(key, Integer::parseInt, defaultValue);
    }

    default Long optLong(String key) {
        return opt(key, Long::parseLong);
    }

    default boolean getBool(String key) {
        return get(key, (value) -> value.equals("1"));
    }

    default boolean getBoolOr(String key, boolean defaultValue) {
        return getOr(key, (value) -> value.equals("1"), defaultValue);
    }

    private static LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeHelper.DATETIME_FORMATTER);
    }

    private static LocalDate parseDate(String value) {
        return LocalDate.parse(value, DateTimeHelper.DATE_FORMATTER);
    }

    default LocalDateTime optDateTime(String key) {
        return opt(key, ValueParser::parseDateTime);
    }

    default LocalDateTime getDateTime(String key) {
        return get(key, ValueParser::parseDateTime);
    }

    default LocalDate getDate(String key) {
        return get(key, ValueParser::parseDate);
    }

    default LocalDate optDate(String key) {
        return opt(key, ValueParser::parseDate);
    }

    default Double getDouble(String key) {
        return get(key, Double::parseDouble);
    }

}
