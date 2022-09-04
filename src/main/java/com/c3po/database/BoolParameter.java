package com.c3po.database;

import lombok.AllArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class BoolParameter extends Parameter {
    final boolean value;

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setInt(index, value ? 1 : 0);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value ? 1 : 0);
    }
}
