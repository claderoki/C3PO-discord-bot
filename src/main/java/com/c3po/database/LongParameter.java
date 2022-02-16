package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongParameter extends Parameter {
    final long value;

    public LongParameter(long value) {
        this.value = value;
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setLong(index, value);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}
