package com.c3po.database;

import com.c3po.helper.ValueParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Result implements ValueParser {
    private final Map<String, String> values;

    public Set<String> getColumns() {
        return values.keySet();
    }

    public Result(ResultSet resultSet, String[] columnLabels) {
        values = new HashMap<>();
        try {
            for (int i = 0; i < columnLabels.length; i++) {
                values.put(columnLabels[i], resultSet.getString(i+1));
            }
        } catch (SQLException ignored) {
        }
    }

    public String optString(String key) {
        return values.get(key);
    }

}
