package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Parameter {
    public abstract void bind(PreparedStatement preparedStatement, int index) throws SQLException;
    public abstract void bind(StringBuilder query);

    public static Parameter from(Object value) {
        if (value == null) {
            return new NullParameter();
        }
        if (value instanceof Long v) {
            return new LongParameter(v);
        }
        if (value instanceof Integer v) {
            return new IntParameter(v);
        }
        return new StringParameter(value.toString());
    }
}
