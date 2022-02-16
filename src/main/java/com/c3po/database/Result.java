package com.c3po.database;

import com.c3po.helper.DurationFormatter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private final Map<String, String> values;

    public Result(ResultSet resultSet, String[] columnLabels) {
        values = new HashMap<>();
        try {
            for (int i = 1; i < columnLabels.length; i++) {
                values.put(columnLabels[i], resultSet.getString(i+1));
            }
        } catch (SQLException e) {
        }
    }

    public String getString(String key) {
        String value = values.get(key);
        if (value == null) {
            throw new RuntimeException("Invalid value");
        }
        return value;
    }

    public int getInt(String key) {
        String value = getString(key);
        return Integer.parseInt(value);
    }

    public boolean getBool(String key) {
        String value = getString(key);
        return value.equals("1");
    }

    public Duration getDuration(String key) {
        String value = getString(key);
        return DurationFormatter.parse(value);
    }



}
