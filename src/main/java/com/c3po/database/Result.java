package com.c3po.database;

import com.c3po.helper.ValueParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Result implements ValueParser {
    private final Map<String, String> values;

    public Result(ResultSet resultSet, String[] columnLabels) {
        values = new HashMap<>();
        try {
            for (int i = 0; i < columnLabels.length; i++) {
                values.put(columnLabels[i], resultSet.getString(i+1));
            }
        } catch (SQLException e) {
        }
    }

    public String getString(String key) {
        String value = values.get(key);
        if (value == null) {
            throw new RuntimeException(key + " is null but is required.");
        }
        return value;
    }

    public String optString(String key) {
        return values.get(key);
    }

}
