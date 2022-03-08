package com.c3po.helper;

import java.time.LocalDateTime;

public interface GenericValueParser {
    String getString(String key);
    int getInt(String key);
    Integer optInt(String key);
    long getLong(String key);
    Long optLong(String key);
    boolean getBool(String key);
    LocalDateTime optDateTime(String key);
    LocalDateTime getDateTime(String key);
}
