package com.c3po.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringParameter extends Parameter<String> {
    public StringParameter(String value) {
        super(value);
    }

    public StringParameter(String value, boolean nullable) {
        super(value, nullable);
    }

    @Override
    public void bind(PreparedStatement preparedStatement, int index) throws SQLException {
        preparedStatement.setString(index, value);
    }

}
