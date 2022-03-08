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
        return switch (value.getClass().toString()) {
            case "class java.lang.Long" -> new LongParameter((long)value);
            case "class java.lang.Integer" -> new IntParameter((int)value);
            default -> new StringParameter(value.toString());
        };
    }
}
