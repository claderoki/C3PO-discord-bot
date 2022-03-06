package com.c3po.database;

import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
@AllArgsConstructor
public class LongParameter extends Parameter {
    final long value;

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setLong(index, value);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}
