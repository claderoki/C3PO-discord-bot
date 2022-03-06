package com.c3po.database;

import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class IntParameter extends Parameter {
    final int value;

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setInt(index, value);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}
