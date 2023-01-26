package com.c3po.database.result;

import com.c3po.helper.ValueParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class Result implements ValueParser {
    private final ColumnContainer columns;
    private final List<String> values;

    public Set<String> getColumns() {
        return columns.keySet();
    }

    public Result(ResultSet resultSet, ColumnContainer columns) {
        this.columns = columns;

        ArrayList<String> values = new ArrayList<>();
        try {
            for (int i = 0; i < columns.size(); i++) {
                values.add(resultSet.getString(i+1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.values = Collections.unmodifiableList(values);
    }

    public String optString(String key) {
        Integer index = columns.get(key);
        if (index == null) {
            return null;
        }
        return values.get(index);
    }
}
