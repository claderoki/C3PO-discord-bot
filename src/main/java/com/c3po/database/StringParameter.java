package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringParameter extends Parameter {
    final String value;

    public StringParameter(String value) {
        this.value = value;
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setString(index, value);
    }

    @Override
    public void bind(StringBuilder query) {
        query.append(value);
    }
}
